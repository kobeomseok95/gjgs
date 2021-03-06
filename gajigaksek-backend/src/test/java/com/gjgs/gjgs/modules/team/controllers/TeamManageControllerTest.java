package com.gjgs.gjgs.modules.team.controllers;

import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.payment.exceptions.OrderErrorCodes;
import com.gjgs.gjgs.modules.payment.exceptions.OrderException;
import com.gjgs.gjgs.modules.team.dtos.DelegateLeaderResponse;
import com.gjgs.gjgs.modules.team.dtos.TeamAppliersResponse;
import com.gjgs.gjgs.modules.team.dtos.TeamExitResponse;
import com.gjgs.gjgs.modules.team.dtos.TeamManageResponse;
import com.gjgs.gjgs.modules.team.exceptions.TeamErrorCodes;
import com.gjgs.gjgs.modules.team.exceptions.TeamException;
import com.gjgs.gjgs.modules.team.services.manage.TeamManageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(
        value = {TeamManageController.class}
)
class TeamManageControllerTest extends RestDocsTestSupport {

    private final String URL = "/api/v1/teams/{teamId}";

    @MockBean TeamManageServiceImpl teamManageService;

    @BeforeEach
    void setUpMockUser() {
        securityUserMockSetting();
    }

    @Test
    @DisplayName("?????? ?????? 1. ??? ????????? ?????? ??????")
    void common_errors_not_leader_exception() throws Exception {

        // given
        when(teamManageService.acceptApplier(any(), any()))
                .thenThrow(new TeamException(TeamErrorCodes.NOT_TEAM_LEADER));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL + "/appliers/{applierId}", 1, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))

                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.code", is("TEAM-403")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("?????? ?????? 2. ??? ????????? ?????? ??????")
    void common_errors_not_team_member_exception() throws Exception {

        // given
        when(teamManageService.acceptApplier(any(), any()))
                .thenThrow(new TeamException(TeamErrorCodes.NOT_TEAM_MEMBER));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL + "/appliers/{applierId}", 1, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.code", is("TEAM-404")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("?????? ?????? 3. ?????? ??????, ?????? ??????, ????????? ??? ?????? ?????? ????????? ?????? ??????")
    void common_errors_has_wait_order_exception() throws Exception {

        // given
        when(teamManageService.changeLeader(1L, 2L))
                .thenThrow(new OrderException(OrderErrorCodes.TEAM_HAS_WAIT_ORDER));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(URL + "/members/{memberId}", 1, 2)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                ;
    }

    @Test
    @DisplayName("??? ?????? ????????????")
    void apply_team() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL + "/appliers", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("teamId").description("????????? ??? ID")
                        )
                ));
    }

    @Test
    @DisplayName("??? ?????? ?????????, ?????? ?????? ?????? ?????? ??????")
    void apply_team_not_found_exception() throws Exception {

        // given
        doThrow(new TeamException(TeamErrorCodes.TEAM_NOT_FOUND))
                .when(teamManageService).applyTeam(any());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL + "/appliers", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.code", is("TEAM-404")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("??? ?????? ?????????, ?????? ?????? ??????????????? ?????? ?????? ?????? ??????")
    void apply_team_should_recruit_status() throws Exception {

        // given
        doThrow(new TeamException(TeamErrorCodes.TEAM_NOT_RECRUITMENT))
                .when(teamManageService).applyTeam(any());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL + "/appliers", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.code", is("TEAM-400")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("??? ?????? ?????????, ???????????? ????????? ?????? ?????? ?????? ?????? ??????")
    void apply_team_should_not_in_team_members() throws Exception {

        // given
        doThrow(new TeamException(TeamErrorCodes.APPLIER_IN_TEAM))
                .when(teamManageService).applyTeam(any());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL + "/appliers", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.code", is("TEAM-409")))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("?????? ?????? ????????????")
    void accept_applier() throws Exception {

        // given
        Long teamId = 1L;
        Long applierId = 1L;
        when(teamManageService.acceptApplier(teamId, applierId))
                .thenReturn(TeamManageResponse.of(teamId, applierId, true));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL + "/appliers/{applierId}", 1, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.teamId", is(1)))
                .andExpect(jsonPath("$.memberId", is(1)))
                .andExpect(jsonPath("$.accept", is(true)))
                .andExpect(jsonPath("$.reject", is(false)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("teamId").description("????????? ??? ID"),
                                parameterWithName("applierId").description("????????? ?????? ID")
                        ),
                        responseFields(
                                fieldWithPath("teamId").type(NUMBER).description("??? ID"),
                                fieldWithPath("memberId").type(NUMBER).description("????????? ?????? ID"),
                                fieldWithPath("accept").type(BOOLEAN).description("?????? ?????? ??????"),
                                fieldWithPath("reject").type(BOOLEAN).description("?????? ?????? ??????")
                        )
                ));

    }

    @Test
    @DisplayName("?????? ?????? ????????????")
    void reject_applier() throws Exception {

        // given
        Long teamId = 1L;
        Long applierId = 1L;
        when(teamManageService.rejectApplier(teamId, applierId))
                .thenReturn(TeamManageResponse.of(teamId, applierId, false));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL + "/appliers/{applierId}", 1, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.teamId", is(1)))
                .andExpect(jsonPath("$.memberId", is(1)))
                .andExpect(jsonPath("$.accept", is(false)))
                .andExpect(jsonPath("$.reject", is(true)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("teamId").description("????????? ??? ID"),
                                parameterWithName("applierId").description("????????? ?????? ID")
                        ),
                        responseFields(
                                fieldWithPath("teamId").type(NUMBER).description("??? ID"),
                                fieldWithPath("memberId").type(NUMBER).description("????????? ?????? ID"),
                                fieldWithPath("accept").type(BOOLEAN).description("?????? ?????? ??????"),
                                fieldWithPath("reject").type(BOOLEAN).description("?????? ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ???????????? ????????????")
    void get_team_appliers() throws Exception {

        // given
        when(teamManageService.getTeamAppliers(any()))
                .thenReturn(createTeamAppliersResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/appliers", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applierList", hasSize(1)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("teamId").description("??????????????? ????????? ??? ID")
                        ),
                        responseFields(
                                fieldWithPath("applierList[0].memberId").type(NUMBER).description("????????? ?????? ID"),
                                fieldWithPath("applierList[0].thumbnailImageUrl").type(STRING).description("????????? ????????? ????????? URL"),
                                fieldWithPath("applierList[0].nickname").type(STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("applierList[0].sex").type(STRING).description("????????? ?????? ??????"),
                                fieldWithPath("applierList[0].age").type(NUMBER).description("????????? ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????????")
    void exclude_team_member() throws Exception {

        // given
        Long teamId = 1L;
        Long memberId = 2L;
        when(teamManageService.excludeMember(teamId, memberId))
                .thenReturn(TeamExitResponse.excludeMember(teamId, memberId));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL + "/members/{memberId}", 1, 2)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.teamId", is(1)))
                .andExpect(jsonPath("$.memberId", is(2)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("teamId").description("????????? ??? ID"),
                                parameterWithName("memberId").description("????????? ?????? ID")
                        ),
                        responseFields(
                                fieldWithPath("teamId").type(NUMBER).description("????????? ??? ID"),
                                fieldWithPath("memberId").type(NUMBER).description("????????? ????????? ID"),
                                fieldWithPath("result").type(STRING).description("??????")
                        )
                ));
    }

    @Test
    @DisplayName("??? ?????????(??????)")
    void exit_team_member() throws Exception {

        // given
        Long teamId = 1L;
        Long exitMemberId = 2L;
        when(teamManageService.exitMember(teamId))
                .thenReturn(TeamExitResponse.exitMember(teamId, exitMemberId));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL + "/members", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.teamId", is(1)))
                .andExpect(jsonPath("$.memberId", is(2)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("teamId").description("?????? ??? ID")
                        ),
                        responseFields(
                                fieldWithPath("teamId").type(NUMBER).description("?????? ??? ID"),
                                fieldWithPath("memberId").type(NUMBER).description("?????? ?????? ID"),
                                fieldWithPath("result").type(STRING).description("??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ????????????")
    void delegate_team_leader() throws Exception {

        // given
        when(teamManageService.changeLeader(1L, 2L))
                .thenReturn(DelegateLeaderResponse.of(1L, 2L, 1L));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(URL + "/members/{memberId}", 1, 2)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.teamId", is(1)))
                .andExpect(jsonPath("$.changedLeaderId", is(2)))
                .andExpect(jsonPath("$.toTeamMemberId", is(1)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("teamId").description("????????? ??? ID"),
                                parameterWithName("memberId").description("????????? ?????? ID")
                        ),
                        responseFields(
                                fieldWithPath("teamId").type(NUMBER).description("????????? ??? ID"),
                                fieldWithPath("changedLeaderId").type(NUMBER).description("????????? ??? ?????? ID"),
                                fieldWithPath("toTeamMemberId").type(NUMBER).description("????????? ??? ?????? ID")
                        )
                ));
    }

    private TeamAppliersResponse createTeamAppliersResponse() {
        return TeamAppliersResponse.builder()
                .applierList(Set.of(
                        TeamAppliersResponse
                                .TeamApplierResponse
                                .builder()
                                .memberId(1L)
                                .nickname("applier1")
                                .sex("M")
                                .age(20)
                                .thumbnailImageUrl("test")
                                .build())).build();
    }
}
