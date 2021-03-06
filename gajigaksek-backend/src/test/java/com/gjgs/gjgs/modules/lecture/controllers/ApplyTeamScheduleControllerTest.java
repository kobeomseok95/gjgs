package com.gjgs.gjgs.modules.lecture.controllers;

import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.lecture.dtos.apply.ApplyLectureTeamRequest;
import com.gjgs.gjgs.modules.lecture.dtos.apply.ApplyLectureTeamResponse;
import com.gjgs.gjgs.modules.lecture.exceptions.ScheduleErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.ScheduleException;
import com.gjgs.gjgs.modules.lecture.services.apply.ApplyScheduleTeamServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static com.gjgs.gjgs.document.utils.RestDocsConfig.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(
        value = { ApplyTeamScheduleController.class}
)
class ApplyTeamScheduleControllerTest extends RestDocsTestSupport {

    @MockBean ApplyScheduleTeamServiceImpl applyTeamService;

    private final String TEAM_URL = "/api/v1/schedules/{scheduleId}";
    private final String TEAM_CANCEL_URL = "/api/v1/schedules/{scheduleId}/teams/{teamId}";

    @BeforeEach
    void setUpMockUser() {
        securityUserMockSetting();
    }

    @Test
    @DisplayName("??? ??????")
    void apply_team() throws Exception {

        // given
        ApplyLectureTeamRequest request = getApplyLectureRequest();
        when(applyTeamService.apply(any(), any())).thenReturn(ApplyLectureTeamResponse.builder()
                .scheduleId(1L)
                .build());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(TEAM_URL, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(request))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(parameterWithName("scheduleId").description("????????? ????????? ID")),
                        requestFields(
                                fieldWithPath("teamId").type(NUMBER).description("????????? ??? ID").attributes(field("constraints", "NOT NULL")),
                                fieldWithPath("lectureId").type(NUMBER).description("????????? ????????? ID").attributes(field("constraints", "NOT NULL"))
                        ),
                        responseFields(
                                fieldWithPath("scheduleId").type(NUMBER).description("?????? ????????? ????????? ID")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("??? ?????? / ????????? ?????? ????????? ID??? ?????? ??????")
    void apply_team_should_not_null_team_id_lecture_id() throws Exception {

        // given
        ApplyLectureTeamRequest request = ApplyLectureTeamRequest.builder().build();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(TEAM_URL, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(request))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("??? ?????? / ??? ????????? ???????????? ??? ?????? ?????? ???????????? ??????.")
    void apply_team_should_team_apply_before_one_hour() throws Exception {

        // given
        ApplyLectureTeamRequest request = getApplyLectureRequest();
        when(applyTeamService.apply(any(), any()))
                .thenThrow(new ScheduleException(ScheduleErrorCodes.SCHEDULE_OVER_TIME));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(TEAM_URL, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(request))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("??? ?????? / ????????? ?????? ???????????? ??????????????? ?????? ??????")
    void apply_team_should_schedule_status_is_recruit() throws Exception {

        // given
        ApplyLectureTeamRequest request = getApplyLectureRequest();
        when(applyTeamService.apply(any(), any()))
                .thenThrow(new ScheduleException(ScheduleErrorCodes.SCHEDULE_NOT_RECRUIT));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(TEAM_URL, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(request))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("??? ?????? / ????????? ?????? ???????????? ?????? ????????? ????????? ?????? ??????")
    void apply_team_should_not_exist_previous_apply_member() throws Exception {

        // given
        ApplyLectureTeamRequest request = getApplyLectureRequest();
        when(applyTeamService.apply(any(), any()))
                .thenThrow(new ScheduleException(ScheduleErrorCodes.PREVIOUS_ENTERED_PARTICIPANT));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(TEAM_URL, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(request))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("??? ?????? / ????????? ????????? ???????????? ????????? ??????")
    void apply_team_should_not_member_is_own_lecture() throws Exception {

        // given
        ApplyLectureTeamRequest request = getApplyLectureRequest();
        when(applyTeamService.apply(any(), any()))
                .thenThrow(new ScheduleException(ScheduleErrorCodes.ACTOR_SHOULD_NOT_DIRECTOR));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(TEAM_URL, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(request))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("??? ?????? / ????????? ?????? ????????? ?????? ??????")
    void apply_team_should_not_over_participants() throws Exception {

        // given
        ApplyLectureTeamRequest request = getApplyLectureRequest();
        when(applyTeamService.apply(any(), any()))
                .thenThrow(new ScheduleException(ScheduleErrorCodes.SCHEDULE_OVER_PARTICIPANTS));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(TEAM_URL, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(request))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("??? ?????? ??????")
    void delete_apply_team() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(TEAM_CANCEL_URL, 1, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("scheduleId").description("?????? ?????? ??? ????????? ID"),
                                parameterWithName("teamId").description("?????? ?????? ??? ??? ID")
                        )
                ));
    }

    private ApplyLectureTeamRequest getApplyLectureRequest() {
        return ApplyLectureTeamRequest.builder()
                .teamId(1L).lectureId(1L).build();
    }
}
