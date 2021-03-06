package com.gjgs.gjgs.modules.team.controllers;

import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.category.repositories.CategoryRepository;
import com.gjgs.gjgs.modules.category.validators.CategoryValidator;
import com.gjgs.gjgs.modules.team.dtos.*;
import com.gjgs.gjgs.modules.team.exceptions.TeamErrorCodes;
import com.gjgs.gjgs.modules.team.exceptions.TeamException;
import com.gjgs.gjgs.modules.team.services.crud.TeamCrudServiceImpl;
import com.gjgs.gjgs.modules.utils.global.GlobalControllerAdvice;
import com.gjgs.gjgs.modules.utils.validators.dayTimeAge.CreateRequestTimeDayAgeValidator;
import com.gjgs.gjgs.modules.utils.validators.dayTimeAge.DayTypeValidator;
import com.gjgs.gjgs.modules.utils.validators.dayTimeAge.TimeTypeValidator;
import com.gjgs.gjgs.modules.zone.exceptions.ZoneErrorCodes;
import com.gjgs.gjgs.modules.zone.exceptions.ZoneException;
import com.gjgs.gjgs.modules.zone.repositories.interfaces.ZoneRepository;
import com.gjgs.gjgs.modules.zone.validators.ZoneValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.List;
import java.util.Set;

import static com.gjgs.gjgs.document.utils.RestDocsConfig.field;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(
        value = {
                TeamController.class, ZoneValidator.class,
                CreateRequestTimeDayAgeValidator.class, TimeTypeValidator.class,
                DayTypeValidator.class, CategoryValidator.class,
                GlobalControllerAdvice.class
        }
)
class TeamControllerTest extends RestDocsTestSupport {

    private final String URL = "/api/v1/teams";

    @MockBean CategoryRepository categoryRepository;
    @MockBean ZoneRepository zoneRepository;
    @MockBean TeamCrudServiceImpl teamService;

    @BeforeEach
    void setUpMockUser() {
        securityUserMockSetting();
    }

    @Test
    @DisplayName("?????? ?????? 1. ?????? ????????? ?????? ??????")
    void team_common_errors_should_need_leader_authorization() throws Exception {

        // given
        when(teamService.deleteTeam(any()))
                .thenThrow(new TeamException(TeamErrorCodes.NOT_TEAM_LEADER));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL + "/{teamId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("?????? ?????? 2. ?????? ???????????? ?????? ??????")
    void common_errors_team_not_found_exception() throws Exception {

        // given
        when(teamService.getTeamDetail(any()))
                .thenThrow(new TeamException(TeamErrorCodes.TEAM_NOT_FOUND));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/{teamId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("??? ????????????")
    void create_team() throws Exception {

        // given
        CreateTeamRequest req = createCreateTeamRequest();
        String body = createJson(req);
        stubbingZoneRepository(req.getZoneId());
        stubbingCategoryRepository(req.getCategoryList());
        when(teamService.createTeam(any()))
                .thenReturn(CreateTeamResponse.builder().createdTeamId("1").build());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(body).contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdTeamId", is("1")))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token")
                        ),
                        requestFields(
                                fieldWithPath("teamName").type(STRING).description("??? ???").attributes(field("constraints", "Not Blank")),
                                fieldWithPath("maxPeople").type(NUMBER).description("??? ?????? ??????").attributes(field("constraints", "1 ~ 4")),
                                fieldWithPath("zoneId").type(NUMBER).description("?????? ID").attributes(field("constraints", "Not Null")),
                                fieldWithPath("dayType").type(STRING).description("?????? ?????? ??????").attributes(field("constraints", "'|'??? ???????????? ??????, Not Blank")),
                                fieldWithPath("timeType").type(STRING).description("?????? ?????? ??????").attributes(field("constraints", "Not Blank")),
                                fieldWithPath("categoryList[]").type(ARRAY).description("???????????? ?????? ????????????").attributes(field("constraints", "?????? ?????? ????????? ???"))
                        ),
                        responseFields(
                                fieldWithPath("createdTeamId").description("????????? ??? ID")
                        )
                ));
    }

    @Test
    @DisplayName("??? ??????, ????????? ????????? ????????? ?????? ?????? ?????? ????????? ??????")
    void create_team_should_follow_day_time_constraints() throws Exception {

        // given
        CreateTeamRequest createTeamRequest = createCreateTeamRequest();
        // set DayType for exception
        createTeamRequest.setDayType("BAR|FOO");
        createTeamRequest.setTimeType("DAWN|DAYBREAK");

        String body = createJson(createTeamRequest);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(body)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATE")));
    }

    @Test
    @DisplayName("??? ??????, DTO??? ?????? ??? null")
    void create_team_should_follow_constraint() throws Exception {

        // given
        String body = createJson(createCreateTeamRequestValidation());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(body)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()", is(7)));
    }

    @Test
    @DisplayName("??? ??????, ????????? ??????id??? ?????? ?????? ????????????")
    void create_team_zone_not_found_exception() throws Exception {

        // given
        CreateTeamRequest request = createCreateTeamRequest();
        String body = createJson(request);
        stubbingCategoryRepository(request.getCategoryList());
        stubbingZoneRepository(request.getZoneId());
        when(teamService.createTeam(any()))
                .thenThrow(new ZoneException(ZoneErrorCodes.ZONE_NOT_FOUND));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(body).contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ZONE-400"));
    }

    @Test
    @DisplayName("??? ??????, ????????? ??????id??? ?????? ?????? ????????????")
    void create_team_category_not_found_exception() throws Exception {

        // given
        CreateTeamRequest request = createCreateTeamRequest();
        String body = createJson(request);
        when(categoryRepository.countCategoryByIdList(request.getCategoryList()))
                .thenReturn((long) request.getCategoryList().size() - 1);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(body)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"));
    }

    @Test
    @DisplayName("??? ??? ????????????")
    void get_my_teams() throws Exception {

        // given
        when(teamService.getMyTeamList())
                .thenReturn(stubMyTeamListResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.myTeamList", hasSize(1)))
                .andExpect(jsonPath("$.myTeamList[0].categoryList", hasSize(1)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token")
                        ),
                        responseFields(
                                fieldWithPath("myTeamList[0].teamId").type(NUMBER).description("??? ID"),
                                fieldWithPath("myTeamList[0].teamName").type(STRING).description("??????"),
                                fieldWithPath("myTeamList[0].applyPeople").type(NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("myTeamList[0].maxPeople").type(NUMBER).description("?????? ????????? ??? ?????? ?????? ??????"),
                                fieldWithPath("myTeamList[0].iamLeader").type(BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("myTeamList[0].categoryList[]").type(ARRAY).description("?????? ?????? ?????? ???????????? ID???")
                        )
                ));
    }

    @Test
    @DisplayName("??? ????????????")
    void update_team() throws Exception {

        // given
        CreateTeamRequest req = createCreateTeamRequest();
        String body = createJson(req);
        stubbingZoneRepository(req.getZoneId());
        stubbingCategoryRepository(req.getCategoryList());
        when(teamService.modifyTeam(any(), any()))
                .thenReturn(ModifyTeamResponse.of(1L));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(URL +  "/{teamId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modifiedTeamId", is(1)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token")
                        ),
                        pathParameters(
                                parameterWithName("teamId").description("????????? ?????? ID")
                        ),
                        requestFields(
                                fieldWithPath("teamName").type(STRING).description("??? ?????? Request??? ??????").attributes(field("constraints", "Not Blank")),
                                fieldWithPath("maxPeople").type(NUMBER).description("??? ?????? Request??? ??????").attributes(field("constraints", "1 ~ 4")),
                                fieldWithPath("zoneId").type(NUMBER).description("??? ?????? Request??? ??????").attributes(field("constraints", "Not Null")),
                                fieldWithPath("dayType").type(STRING).description("??? ?????? Request??? ??????").attributes(field("constraints", "'|'??? ???????????? ??????, Not Blank")),
                                fieldWithPath("timeType").type(STRING).description("??? ?????? Request??? ??????").attributes(field("constraints", "Not Blank")),
                                fieldWithPath("categoryList[]").type(ARRAY).description("??? ?????? Request??? ??????").attributes(field("constraints", "?????? ?????? ????????? ???"))
                        ),
                        responseFields(
                                fieldWithPath("modifiedTeamId").type(NUMBER).description("????????? ??? ID")
                        )
                ));
    }

    @Test
    @DisplayName("??? ??????, ?????? ???????????? ?????? ?????? ?????? ?????? ?????? ??????")
    void update_team_should_modify_member_count_over_current_member_count() throws Exception {

        // given
        CreateTeamRequest request = createCreateTeamRequest();
        String body = createJson(request);
        when(zoneRepository.existsById(any())).thenReturn(true);
        when(categoryRepository.countCategoryByIdList(any())).thenReturn((long) request.getCategoryList().size());
        when(teamService.modifyTeam(any(), any()))
                .thenThrow(new TeamException(TeamErrorCodes.TEAM_CURRENT_PEOPLE_HIGHER));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(URL + "/{teamId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(body)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("TEAM-400")))
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("??? ?????? ??????")
    void get_team_detail() throws Exception {

        // given
        when(teamService.getTeamDetail(any()))
                .thenReturn(stubMyTeamDetailResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/{teamId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName", is("?????????1")))
                .andExpect(jsonPath("$.day", is("MON")))
                .andExpect(jsonPath("$.time", is("MORNING")))
                .andExpect(jsonPath("$.applyPeople", is(2)))
                .andExpect(jsonPath("$.maxPeople", is(3)))
                .andExpect(jsonPath("$.zoneId", is(1)))
                .andExpect(jsonPath("$.iamLeader", is(true)))
                .andExpect(jsonPath("$.teamsLeader.nickname", is("leader")))
                .andExpect(jsonPath("$.categoryList", hasSize(2)))
                .andExpect(jsonPath("$.teamMemberList", hasSize(1)))
                .andExpect(jsonPath("$.favoriteLectureList", hasSize(1)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("teamId").description("?????? ????????? ??? ID")
                        ),
                        responseFields(
                                fieldWithPath("teamName").type(STRING).description("??? ???"),
                                fieldWithPath("day").type(STRING).description("?????? ?????? ??????"),
                                fieldWithPath("time").type(STRING).description("?????? ?????? ?????????"),
                                fieldWithPath("applyPeople").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("maxPeople").type(NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("zoneId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("iamLeader").type(BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("teamsLeader.memberId").type(NUMBER).description("????????? ?????? ID"),
                                fieldWithPath("teamsLeader.imageURL").type(STRING).description("????????? ????????? ?????? URL"),
                                fieldWithPath("teamsLeader.nickname").type(STRING).description("????????? ?????????"),
                                fieldWithPath("teamsLeader.sex").type(STRING).description("????????? ??????"),
                                fieldWithPath("teamsLeader.age").type(NUMBER).description("????????? ??????"),
                                fieldWithPath("teamsLeader.text").type(STRING).description("????????? ?????????"),
                                fieldWithPath("categoryList[]").type(ARRAY).description("?????? ???????????? ?????????"),
                                fieldWithPath("teamMemberList[0].memberId").type(NUMBER).description("?????? ?????? ?????? ID"),
                                fieldWithPath("teamMemberList[0].imageURL").type(STRING).description("?????? ?????? ????????? ????????? ?????? URL"),
                                fieldWithPath("teamMemberList[0].nickname").type(STRING).description("?????? ?????? ????????? ?????????"),
                                fieldWithPath("teamMemberList[0].sex").type(STRING).description("?????? ?????? ????????? ??????"),
                                fieldWithPath("teamMemberList[0].age").type(NUMBER).description("?????? ?????? ????????? ??????"),
                                fieldWithPath("teamMemberList[0].text").type(STRING).description("?????? ?????? ????????? ?????????"),
                                fieldWithPath("favoriteLectureList[0].lectureId").type(NUMBER).description("?????? ?????? ????????? ID"),
                                fieldWithPath("favoriteLectureList[0].lecturesZoneId").type(NUMBER).description("?????? ?????? ????????? ?????? ID"),
                                fieldWithPath("favoriteLectureList[0].lecturesTitle").type(STRING).description("?????? ?????? ????????? ??????"),
                                fieldWithPath("favoriteLectureList[0].lecturesPrice").type(NUMBER).description("?????? ?????? ????????? ?????? ??????"),
                                fieldWithPath("favoriteLectureList[0].lecturesImageURL").type(STRING).description("????????? ?????? URL"),
                                fieldWithPath("favoriteLectureList[0].myFavoriteLecture").type(BOOLEAN).description("?????? ?????? ????????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("??? ????????????")
    void delete_team() throws Exception {

        // given
        Long teamId = 1L;
        when(teamService.deleteTeam(teamId))
                .thenReturn(TeamExitResponse.teamDelete(teamId, 100L));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL + "/{teamId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.teamId", is(1)))
                .andExpect(jsonPath("$.memberId", is(100)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("teamId").description("????????? ??? ID")
                        ),
                        responseFields(
                                fieldWithPath("teamId").type(NUMBER).description("????????? ??? ID"),
                                fieldWithPath("memberId").type(NUMBER).description("????????? ?????? ID"),
                                fieldWithPath("result").type(STRING).description("??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ????????? ??? ????????????")
    void get_my_lead_teams() throws Exception {

        // given
        when(teamService.getMyLeadTeamWithBulletinLecture())
                .thenReturn(createMyLeadTeamResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/lead")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.myLeadTeams", hasSize(1)))
                .andExpect(jsonPath("$.myLeadTeams[0].teamId", is(1)))
                .andExpect(jsonPath("$.myLeadTeams[0].teamName", is("test")))
                .andExpect(jsonPath("$.myLeadTeams[0].hasBulletin", is(true)))
                .andExpect(jsonPath("$.myLeadTeams[0].teamsRecruit", is(true)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("myLeadTeams[0].teamId").type(NUMBER).description("?????? ????????? ?????? ID"),
                                fieldWithPath("myLeadTeams[0].teamName").type(STRING).description("?????? ????????? ??????"),
                                fieldWithPath("myLeadTeams[0].hasBulletin").type(BOOLEAN).description("?????? ????????? ?????? ???????????? ????????? ??????"),
                                fieldWithPath("myLeadTeams[0].teamsRecruit").type(BOOLEAN).description("?????? ????????? ?????? ??????????????? ??????"),
                                fieldWithPath("myLeadTeams[0].bulletinData.bulletinId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("myLeadTeams[0].bulletinData.bulletinTitle").type(STRING).description("????????? ??????"),
                                fieldWithPath("myLeadTeams[0].bulletinData.age").type(STRING).description("???????????? ?????????"),
                                fieldWithPath("myLeadTeams[0].bulletinData.time").type(STRING).description("???????????? ?????????"),
                                fieldWithPath("myLeadTeams[0].bulletinData.text").type(STRING).description("????????? ?????????"),
                                fieldWithPath("myLeadTeams[0].bulletinData.day").type(STRING).description("???????????? ??????"),
                                fieldWithPath("myLeadTeams[0].lectureData.lectureId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("myLeadTeams[0].lectureData.lectureTitle").type(STRING).description("????????? ??????"),
                                fieldWithPath("myLeadTeams[0].lectureData.lectureImageUrl").type(STRING).description("????????? ????????? URL"),
                                fieldWithPath("myLeadTeams[0].lectureData.zoneId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("myLeadTeams[0].lectureData.categoryId").type(NUMBER).description("?????? ???????????? ID"),
                                fieldWithPath("myLeadTeams[0].lectureData.priceOne").type(NUMBER).description("????????? 1??? ?????? ??????"),
                                fieldWithPath("myLeadTeams[0].lectureData.priceTwo").type(NUMBER).description("????????? 2??? ?????? ??????"),
                                fieldWithPath("myLeadTeams[0].lectureData.priceThree").type(NUMBER).description("????????? 3??? ?????? ??????"),
                                fieldWithPath("myLeadTeams[0].lectureData.priceFour").type(NUMBER).description("????????? 4??? ?????? ??????")
                        )
                ));
    }

    private MyLeadTeamsResponse createMyLeadTeamResponse() {
        return MyLeadTeamsResponse.builder()
                .myLeadTeams(List.of(MyLeadTeamsResponse.MyLeadTeamsWithBulletin.builder()
                        .teamId(1L).teamName("test").teamsRecruit(true).hasBulletin(true)
                        .bulletinData(
                                MyLeadTeamsResponse.BulletinData.builder()
                                        .bulletinId(1L)
                                        .bulletinTitle("test")
                                        .age("test")
                                        .time("test")
                                        .text("test")
                                        .day("test")
                                        .build()
                        )
                        .lectureData(
                                MyLeadTeamsResponse.LectureData.builder()
                                        .lectureId(1L)
                                        .lectureTitle("test")
                                        .lectureImageUrl("test")
                                        .zoneId(1L)
                                        .categoryId(1L)
                                        .priceOne(1000)
                                        .priceTwo(1000)
                                        .priceThree(1000)
                                        .priceFour(1000)
                                        .build()
                        )
                        .build()))
                .build();
    }

    private void stubbingCategoryRepository(List<Long> categoryList) {
        when(categoryRepository.countCategoryByIdList(categoryList)).thenReturn((long) categoryList.size());
    }

    private void stubbingZoneRepository(Long zoneId) {
        when(zoneRepository.existsById(zoneId)).thenReturn(true);
    }

    private CreateTeamRequest createCreateTeamRequestValidation() {
        return CreateTeamRequest.builder().build();
    }

    private CreateTeamRequest createCreateTeamRequest() {
        return CreateTeamRequest.builder()
                .teamName("test")
                .maxPeople(4)
                .zoneId(1L)
                .timeType("MORNING")
                .dayType("MON|TUE")
                .categoryList(List.of(1L, 2L, 3L))
                .build();
    }

    private MyTeamListResponse stubMyTeamListResponse() {
        return MyTeamListResponse
                .builder()
                .myTeamList(
                        Set.of(MyTeamListResponse.MyTeam.builder()
                                .teamId(1L)
                                .teamName("?????????1")
                                .applyPeople(3)
                                .maxPeople(4)
                                .iAmLeader(false)
                                .categoryList(Set.of(1L))
                                .build())
                ).build();
    }

    private TeamDetailResponse stubMyTeamDetailResponse() {
        return TeamDetailResponse.builder()
                .teamName("?????????1")
                .day("MON")
                .time("MORNING")
                .applyPeople(2)
                .maxPeople(3)
                .zoneId(1L)
                .iAmLeader(true)
                .teamsLeader(
                        TeamDetailResponse.TeamMembers.builder()
                                .memberId(1L)
                                .imageURL("test")
                                .nickname("leader")
                                .sex("M")
                                .age(20)
                                .text("test")
                                .build())
                .categoryList(Set.of(1L, 2L))
                .teamMemberList(Set.of(
                        TeamDetailResponse.TeamMembers.builder()
                                .memberId(1L)
                                .imageURL("test")
                                .nickname("member1")
                                .sex("M")
                                .age(20)
                                .text("test")
                                .build()))
                .favoriteLectureList(Set.of(
                        TeamDetailResponse.FavoriteLecture.builder()
                                .lectureId(1L)
                                .lecturesZoneId(1L)
                                .lecturesTitle("lecture1")
                                .lecturesPrice(20000)
                                .lecturesImageURL("test")
                                .myFavoriteLecture(false)
                                .build()))
                .build();
    }
}
