package com.gjgs.gjgs.modules.bulletin.controller;

import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.bulletin.dto.BulletinChangeRecruitResponse;
import com.gjgs.gjgs.modules.bulletin.dto.BulletinIdResponse;
import com.gjgs.gjgs.modules.bulletin.dto.CreateBulletinRequest;
import com.gjgs.gjgs.modules.bulletin.dto.RecruitStatus;
import com.gjgs.gjgs.modules.bulletin.dto.search.BulletinSearchResponse;
import com.gjgs.gjgs.modules.bulletin.services.BulletinServiceImpl;
import com.gjgs.gjgs.modules.bulletin.validators.AgeValidator;
import com.gjgs.gjgs.modules.dummy.BulletinDtoDummy;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureException;
import com.gjgs.gjgs.modules.team.exceptions.TeamErrorCodes;
import com.gjgs.gjgs.modules.team.exceptions.TeamException;
import com.gjgs.gjgs.modules.utils.exceptions.search.SearchValidator;
import com.gjgs.gjgs.modules.utils.validators.dayTimeAge.CreateRequestTimeDayAgeValidator;
import com.gjgs.gjgs.modules.utils.validators.dayTimeAge.DayTypeValidator;
import com.gjgs.gjgs.modules.utils.validators.dayTimeAge.TimeTypeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.DocUrl;
import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.generateLinkCode;
import static com.gjgs.gjgs.document.utils.RestDocsConfig.field;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = {BulletinController.class, SearchValidator.class,
                CreateRequestTimeDayAgeValidator.class, TimeTypeValidator.class,
                DayTypeValidator.class, AgeValidator.class}
)
class BulletinControllerTest extends RestDocsTestSupport {

    @MockBean
    BulletinServiceImpl bulletinService;

    @BeforeEach
    void set_up_mock_user() {
        securityUserMockSetting();
    }

    @Test
    @DisplayName("????????? ??????")
    void create_bulletin() throws Exception {

        // given
        String body = createJson(create_create_bulletin_request());
        when(bulletinService.createBulletin(any()))
                .thenReturn(BulletinIdResponse.builder()
                        .bulletinId(1L)
                        .teamId(1L).lectureId(1L)
                        .build());

        // when, then
        mockMvc.perform(post("/api/v1/bulletins")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bulletinId", is(1)))
                .andExpect(jsonPath("$.teamId", is(1)))
                .andExpect(jsonPath("$.lectureId", is(1)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestFields(
                                fieldWithPath("teamId").type(NUMBER).description("???????????? ????????? ?????? ID").attributes(field("constraints", "?????? ID??? ?????? ?????? ?????? ??????")),
                                fieldWithPath("title").type(STRING).description("????????? ??????").attributes(field("constraints", "????????? ????????? 5??? ??????(?????? ??????)??? ?????? ?????? ??????")),
                                fieldWithPath("age").type(STRING).description(generateLinkCode(DocUrl.BULLETIN_AGE)),
                                fieldWithPath("timeType").type(STRING).description(generateLinkCode(DocUrl.TIME_TYPE)),
                                fieldWithPath("text").type(STRING).description("????????? ?????????").attributes(field("constraints", "?????? 10???, ?????? 500??? ?????? ??????")),
                                fieldWithPath("lectureId").type(NUMBER).description("????????? ???????????? ID").attributes(field("constraints", "????????? ID??? ???????????? ?????? ?????? ?????? ??????")),
                                fieldWithPath("dayType").type(STRING).description(generateLinkCode(DocUrl.DAY_TYPE))),
                        responseFields(
                                fieldWithPath("bulletinId").type(NUMBER).description("????????? ?????? ???????????? ID"),
                                fieldWithPath("lectureId").type(NUMBER).description("????????? ???????????? ID"),
                                fieldWithPath("teamId").type(NUMBER).description("???????????? ????????? ?????? ID")
                        )));
    }

    @Test
    @DisplayName("????????? ?????? NotNull ?????????")
    void create_bulletin_should_not_null_required_attribute_validation() throws Exception {

        // given
        String body = createJson(create_create_bulletin_not_valid_request());

        // when, then
        mockMvc.perform(post("/api/v1/bulletins")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("????????? ????????? title??? text??? ?????? ??? ??????????????? ????????? ?????? ??????")
    void create_bulletin_should_text_length_size_minimum() throws Exception {

        // given
        CreateBulletinRequest request = create_create_bulletin_request();
        request.setTitle("g");
        request.setText("g");
        String body = createJson(request);

        // when, then
        mockMvc.perform(post("/api/v1/bulletins")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }

    @Test
    @DisplayName("????????? ????????? ??????, ??????, ?????? ????????? ?????? ?????? ?????? ??? ??????")
    void create_bulletin_should_follow_constraint_time_age_value() throws Exception {

        // given
        CreateBulletinRequest request = create_create_bulletin_request();
        request.setAge("g");
        request.setTimeType("g");
        request.setDayType("g");
        String body = createJson(request);

        // when, then
        mockMvc.perform(post("/api/v1/bulletins")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(3)));
    }

    @Test
    @DisplayName("????????? ???????????? ?????? ??? ?????? ??????")
    void create_bulletin_should_pick_lecture() throws Exception {

        // given
        String body = createJson(create_create_bulletin_request());
        when(bulletinService.createBulletin(any()))
                .thenThrow(new LectureException(LectureErrorCodes.LECTURE_NOT_FOUND));

        // when, then
        mockMvc.perform(post("/api/v1/bulletins")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("LECTURE-404")));
    }

    @Test
    @DisplayName("????????? ??????(????????????)")
    void delete_bulletin() throws Exception {

        // given
        when(bulletinService.deleteBulletin(any()))
                .thenReturn(BulletinIdResponse.of(1L));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/bulletins/{bulletinId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bulletinId", is(1)))
                .andExpect(jsonPath("$.lectureId").isEmpty())
                .andExpect(jsonPath("$.teamId").isEmpty())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("bulletinId").description("????????? ????????? ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("bulletinId").type(NUMBER).description("????????? ?????? ???????????? ID"),
                                fieldWithPath("lectureId").type(NULL).optional().description("null"),
                                fieldWithPath("teamId").type(NULL).optional().description("null")
                        )));
    }

    @Test
    @DisplayName("????????? ????????????")
    void update_bulletin() throws Exception {

        // given
        String body = createJson(create_create_bulletin_request());
        when(bulletinService.modifyBulletin(any(), any()))
                .thenReturn(BulletinIdResponse.of(1L, 1L));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/bulletins/{bulletinId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bulletinId", is(1)))
                .andExpect(jsonPath("$.lectureId", is(1)))
                .andExpect(jsonPath("$.teamId").isEmpty())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("bulletinId").description("???????????? ????????? ????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("teamId").type(NUMBER).description("???????????? ????????? ?????? ID").attributes(field("constraints", "?????? ID??? ?????? ?????? ?????? ??????")),
                                fieldWithPath("title").type(STRING).description("????????? ??????").attributes(field("constraints", "????????? ????????? 5??? ??????(?????? ??????)??? ?????? ?????? ??????")),
                                fieldWithPath("age").type(STRING).description(generateLinkCode(DocUrl.BULLETIN_AGE)),
                                fieldWithPath("timeType").type(STRING).description(generateLinkCode(DocUrl.TIME_TYPE)),
                                fieldWithPath("text").type(STRING).description("????????? ?????????").attributes(field("constraints", "?????? 10???, ?????? 500??? ?????? ??????")),
                                fieldWithPath("lectureId").type(NUMBER).description("????????? ???????????? ID").attributes(field("constraints", "????????? ID??? ???????????? ?????? ?????? ?????? ??????")),
                                fieldWithPath("dayType").type(STRING).description(generateLinkCode(DocUrl.DAY_TYPE))
                        ),
                        responseFields(
                                fieldWithPath("bulletinId").type(NUMBER).description("????????? ?????? ???????????? ID"),
                                fieldWithPath("lectureId").type(NUMBER).description("????????? ????????? ID"),
                                fieldWithPath("teamId").type(NULL).optional().description("?????? ?????? ??? ???????????? null")
                )));
    }

    @Test
    @DisplayName("????????? ????????????(?????????), ????????? null ??????")
    void read_bulletin() throws Exception {

        // given
        when(bulletinService.getBulletins(any(), any()))
                .thenReturn(create_search_response());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/bulletins?categoryIdList=1,2,3&zoneId=1&page=0&size=12&keyword=?????????")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.pageable.offset", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(12)))
                .andExpect(jsonPath("$.totalPages", is(9)))
                .andExpect(jsonPath("$.first", is(true)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestParameters(
                                parameterWithName("categoryIdList").description("????????? ?????? ????????????"),
                                parameterWithName("zoneId").description("????????? ??????"),
                                parameterWithName("page").description("????????? ????????? 0?????? ??????"),
                                parameterWithName("size").description("??? ???????????? ????????? ???????????? ?????????"),
                                parameterWithName("keyword").description("???????????? ?????????, ????????? ?????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("content[].myFavorite").type(BOOLEAN).description("?????? ?????? ???????????? ??????"),
                                fieldWithPath("content[].bulletinId").type(NUMBER).description("????????? ???????????? ID"),
                                fieldWithPath("content[].lectureImageUrl").type(STRING).description("???????????? ?????????"),
                                fieldWithPath("content[].zoneId").type(NUMBER).description("??????"),
                                fieldWithPath("content[].categoryId").type(NUMBER).description("?????? ????????????"),
                                fieldWithPath("content[].bulletinTitle").type(STRING).description("???????????? ??????"),
                                fieldWithPath("content[].age").type(STRING).description("???????????? ???????????? ?????????"),
                                fieldWithPath("content[].time").type(STRING).description("???????????? ?????? ?????????"),
                                fieldWithPath("content[].nowMembers").type(NUMBER).description("?????? ?????? ?????? ??????"),
                                fieldWithPath("content[].maxMembers").type(NUMBER).description("?????? ?????? ??????")
                        ).and(pageDescriptor())
                ));
    }

    @Test
    @DisplayName("????????? ???????????? / ???????????? ??? ???????????? ?????? ??????")
    void get_bulletin_should_not_keyword_is_not_blank() throws Exception {

        // when, then
        mockMvc.perform(get("/api/v1/bulletins?keyword=")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("????????? ?????? ????????????")
    void get_bulletin_detail() throws Exception {

        // given
        when(bulletinService.getBulletinDetails(any()))
                .thenReturn(BulletinDtoDummy.createBulletinDetailResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/bulletins/{bulletinId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.bulletinId", is(1)))
                .andExpect(jsonPath("$.bulletinTitle", is("testTitle")))
                .andExpect(jsonPath("$.day", is("testDay")))
                .andExpect(jsonPath("$.time", is("testTime")))
                .andExpect(jsonPath("$.age", is("TWENTYFIVE_TO_THIRTY")))
                .andExpect(jsonPath("$.bulletinText", is("testText")))
                .andExpect(jsonPath("$.bulletinsLecture.lectureId", is(2)))
                .andExpect(jsonPath("$.bulletinsLecture.myFavoriteLecture", is(false)))
                .andExpect(jsonPath("$.bulletinsLecture.lecturesZoneId", is(3)))
                .andExpect(jsonPath("$.bulletinsLecture.lecturesCategoryId", is(4)))
                .andExpect(jsonPath("$.bulletinsLecture.lecturesThumbnailUrl", is("testImageUrl")))
                .andExpect(jsonPath("$.bulletinsLecture.lectureName", is("testLectureTitle")))
                .andExpect(jsonPath("$.bulletinsLecture.priceOne", is(100)))
                .andExpect(jsonPath("$.bulletinsLecture.priceTwo", is(200)))
                .andExpect(jsonPath("$.bulletinsLecture.priceThree", is(300)))
                .andExpect(jsonPath("$.bulletinsLecture.priceFour", is(400)))
                .andExpect(jsonPath("$.bulletinsTeam.teamId", is(10)))
                .andExpect(jsonPath("$.bulletinsTeam.iamLeader", is(false)))
                .andExpect(jsonPath("$.bulletinsTeam.currentPeople", is(3)))
                .andExpect(jsonPath("$.bulletinsTeam.maxPeople", is(4)))
                .andExpect(jsonPath("$.bulletinsTeam.leader.memberId", is(1)))
                .andExpect(jsonPath("$.bulletinsTeam.leader.imageUrl", is("testUrl1")))
                .andExpect(jsonPath("$.bulletinsTeam.leader.nickname", is("testNick1")))
                .andExpect(jsonPath("$.bulletinsTeam.leader.sex", is("M")))
                .andExpect(jsonPath("$.bulletinsTeam.leader.age", is(21)))
                .andExpect(jsonPath("$.bulletinsTeam.leader.text", is("testText1")))
                .andExpect(jsonPath("$.bulletinsTeam.members", hasSize(2)))
                .andExpect(jsonPath("$.bulletinsTeam.members[0].memberId", is(2)))
                .andExpect(jsonPath("$.bulletinsTeam.members[0].imageUrl", is("testUrl2")))
                .andExpect(jsonPath("$.bulletinsTeam.members[0].nickname", is("testNick2")))
                .andExpect(jsonPath("$.bulletinsTeam.members[0].sex", is("M")))
                .andExpect(jsonPath("$.bulletinsTeam.members[0].age", is(22)))
                .andExpect(jsonPath("$.bulletinsTeam.members[0].text", is("testText2")))
                .andExpect(jsonPath("$.bulletinsTeam.members[1].memberId", is(3)))
                .andExpect(jsonPath("$.bulletinsTeam.members[1].imageUrl", is("testUrl3")))
                .andExpect(jsonPath("$.bulletinsTeam.members[1].nickname", is("testNick3")))
                .andExpect(jsonPath("$.bulletinsTeam.members[1].sex", is("M")))
                .andExpect(jsonPath("$.bulletinsTeam.members[1].age", is(23)))
                .andExpect(jsonPath("$.bulletinsTeam.members[1].text", is("testText3")))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("bulletinId").description("?????? ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("bulletinId").type(NUMBER).description("?????? ????????? ID"),
                                fieldWithPath("bulletinTitle").type(STRING).description("????????? ??????"),
                                fieldWithPath("day").type(STRING).description("???????????? ?????? ?????? ??????"),
                                fieldWithPath("time").type(STRING).description("???????????? ?????? ?????? ?????????"),
                                fieldWithPath("age").type(STRING).description("???????????? ????????? ?????? ?????????"),
                                fieldWithPath("bulletinText").type(STRING).description("???????????? ?????????"),
                                fieldWithPath("bulletinsLecture.lectureId").type(NUMBER).description("????????? ????????? ID"),
                                fieldWithPath("bulletinsLecture.myFavoriteLecture").type(BOOLEAN).description("?????? ????????? ??? ??????????????? ??????"),
                                fieldWithPath("bulletinsLecture.lecturesZoneId").type(NUMBER).description("???????????? ??????"),
                                fieldWithPath("bulletinsLecture.lecturesCategoryId").type(NUMBER).description("???????????? ????????????"),
                                fieldWithPath("bulletinsLecture.lecturesThumbnailUrl").type(STRING).description("????????? ????????? URL"),
                                fieldWithPath("bulletinsLecture.lectureName").type(STRING).description("????????? ????????? ??????"),
                                fieldWithPath("bulletinsLecture.priceOne").type(NUMBER).description("????????? ???????????? 1??? ?????? ??????"),
                                fieldWithPath("bulletinsLecture.priceTwo").type(NUMBER).description("????????? ???????????? 2??? ?????? ??????"),
                                fieldWithPath("bulletinsLecture.priceThree").type(NUMBER).description("????????? ???????????? 3??? ?????? ??????"),
                                fieldWithPath("bulletinsLecture.priceFour").type(NUMBER).description("????????? ???????????? 4??? ?????? ??????"),
                                fieldWithPath("bulletinsTeam.teamId").type(NUMBER).description("???????????? ?????? ID"),
                                fieldWithPath("bulletinsTeam.iamLeader").type(BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("bulletinsTeam.currentPeople").type(NUMBER).description("?????? ?????? ?????? ?????? ???"),
                                fieldWithPath("bulletinsTeam.maxPeople").type(NUMBER).description("?????? ?????? ?????? ?????? ???"),
                                fieldWithPath("bulletinsTeam.leader.memberId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("bulletinsTeam.leader.imageUrl").type(STRING).description("????????? ????????? URL"),
                                fieldWithPath("bulletinsTeam.leader.nickname").type(STRING).description("????????? ?????????"),
                                fieldWithPath("bulletinsTeam.leader.sex").type(STRING).description("????????? ??????"),
                                fieldWithPath("bulletinsTeam.leader.age").type(NUMBER).description("????????? ??????"),
                                fieldWithPath("bulletinsTeam.leader.text").type(STRING).description("????????? ?????????"),
                                fieldWithPath("bulletinsTeam.members[0].memberId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("bulletinsTeam.members[0].imageUrl").type(STRING).description("????????? ????????? URL"),
                                fieldWithPath("bulletinsTeam.members[0].nickname").type(STRING).description("????????? ?????????"),
                                fieldWithPath("bulletinsTeam.members[0].sex").type(STRING).description("????????? ??????"),
                                fieldWithPath("bulletinsTeam.members[0].age").type(NUMBER).description("????????? ??????"),
                                fieldWithPath("bulletinsTeam.members[0].text").type(STRING).description("????????? ?????????")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("????????? ?????? ????????? ????????????")
    void update_bulletin_change_status() throws Exception {

        // given
        when(bulletinService.changeRecruitStatus(1L))
                .thenReturn(BulletinChangeRecruitResponse.builder()
                        .bulletinId(1L).recruitStatus(RecruitStatus.RECRUIT.name())
                        .build());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/bulletins/{bulletinId}/recruit", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.bulletinId", is(1)))
                .andExpect(jsonPath("$.recruitStatus", is("RECRUIT")))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("bulletinId").description("?????? ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("bulletinId").type(NUMBER).description("?????? ????????? ????????? ?????????"),
                                fieldWithPath("recruitStatus").type(STRING).description("????????? ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ????????? ???????????? / ????????? ??? ????????? ??????")
    void update_bulletin_should_not_team_members_full() throws Exception {

        // given
        when(bulletinService.changeRecruitStatus(any()))
                .thenThrow(new TeamException(TeamErrorCodes.TEAM_MEMBER_IS_MAX));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/bulletins/{bulletinId}/recruit", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.code", is("TEAM-400")))
                .andExpect(status().isBadRequest());
    }

    private Page<BulletinSearchResponse> create_search_response() {
        return new PageImpl<>(BulletinDtoDummy.createBulletinSearchResponseListForOne(),
                PageRequest.of(0, 12),
                100);
    }

    private CreateBulletinRequest create_create_bulletin_not_valid_request() {
        return CreateBulletinRequest.builder()
                .text("test")
                .build();
    }

    private CreateBulletinRequest create_create_bulletin_request() {
        return CreateBulletinRequest.builder()
                .teamId(1L)
                .title("testtest")
                .age("TWENTY_TO_TWENTYFIVE")
                .timeType("MORNING")
                .text("testststest")
                .lectureId(1L)
                .dayType("MON")
                .build();
    }
}
