package com.gjgs.gjgs.modules.question.controller;

import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.member.dto.mypage.QuestionMainTextModifyRequest;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.question.dto.*;
import com.gjgs.gjgs.modules.question.services.QuestionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.DocUrl.QUESTION_RESPONSE;
import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.generateLinkCode;
import static com.gjgs.gjgs.document.utils.RestDocsConfig.field;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(
        value = {QuestionController.class}
)
class QuestionControllerTest extends RestDocsTestSupport {

    private final String URL = "/api/v1/questions";

    @MockBean QuestionServiceImpl questionService;

    @WithMockUser
    @Test
    @DisplayName("????????? ??????")
    void create_question() throws Exception {

        // given
        securityUserMockSetting();
        String body = createJson(createQuestionRequest());
        when(questionService.createQuestion(any()))
                .thenReturn(QuestionResponse.builder()
                        .lectureId(1L)
                        .questionId(1L)
                        .result(QuestionResult.QUESTION.getDescription())
                        .build());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.lectureId", is(1)))
                .andExpect(jsonPath("$.questionId", is(1)))
                .andExpect(jsonPath("$.result", is(QuestionResult.QUESTION.getDescription())))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestFields(
                                fieldWithPath("lectureId").type(NUMBER).description("????????? ????????? ????????? ID").attributes(field("constraints", "Not Null")),
                                fieldWithPath("questionForm.mainText").type(STRING).description("????????? ??????").attributes(field("constraints", "Not Null"))
                        ),
                        responseFields(
                                fieldWithPath("lectureId").type(NUMBER).description("????????? ????????? ID"),
                                fieldWithPath("questionId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("result").description(generateLinkCode(QUESTION_RESPONSE))
                        )
                ))
        ;
    }

    @Test
    @DisplayName("????????? ?????? / ????????? ????????? ID, ???????????? ???????????? ?????? ??????")
    void create_question_should_require_lecture_id_and_main_text() throws Exception {

        // given
        securityUserMockSetting();
        String body = createJson(QuestionRequest.builder().build());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void get_question() throws Exception {

        // given
        when(questionService.getQuestion(any()))
                .thenReturn(QuestionDetailResponse.builder()
                        .lectureId(1L)
                        .questionId(1L)
                        .questionStatus("COMPLETE")
                        .questionDetail(QuestionDetailResponse.QuestionDetail.builder()
                                .questionMainText("test")
                                .questionerNickname("test")
                                .questionerProfileImageUrl("test")
                                .questionDate(LocalDateTime.now())
                                .build())
                        .answerDetail(QuestionDetailResponse.AnswerDetail.builder()
                                .directorNickname("test")
                                .replyText("test")
                                .directorProfileImageUrl("test")
                                .build())
                        .build());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/{questionId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.lectureId", is(1)))
                .andExpect(jsonPath("$.questionId", is(1)))
                .andExpect(jsonPath("$.questionStatus", is("COMPLETE")))
                .andExpect(jsonPath("$.questionDetail.questionerNickname", is("test")))
                .andExpect(jsonPath("$.questionDetail.questionMainText", is("test")))
                .andExpect(jsonPath("$.questionDetail.questionerProfileImageUrl", is("test")))
                .andExpect(jsonPath("$.answerDetail.directorProfileImageUrl", is("test")))
                .andExpect(jsonPath("$.answerDetail.directorNickname", is("test")))
                .andExpect(jsonPath("$.answerDetail.replyText", is("test")))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("questionId").description("????????? ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("lectureId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("questionId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("questionStatus").type(STRING).description("???????????? ?????? ?????? ??????"),
                                fieldWithPath("questionDetail.questionerNickname").type(STRING).description("????????? ?????????"),
                                fieldWithPath("questionDetail.questionMainText").type(STRING).description("?????????"),
                                fieldWithPath("questionDetail.questionerProfileImageUrl").type(STRING).description("????????? ????????? URL"),
                                fieldWithPath("questionDetail.questionDate").type(STRING).description("????????? ??????"),
                                fieldWithPath("answerDetail.directorProfileImageUrl").type(STRING).description("????????? ????????? URL"),
                                fieldWithPath("answerDetail.directorNickname").type(STRING).description("????????? ?????????"),
                                fieldWithPath("answerDetail.replyText").type(STRING).description("?????????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ??? ??????")
    void reply_question() throws Exception {

        // given
        securityDirectorMockSetting();
        String body = createJson(createAnswerRequest());
        when(questionService.putAnswer(any(), any()))
                .thenReturn(QuestionResponse
                        .createAnswer(1L, 1L));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.put(URL + "/{questionId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.lectureId", is(1)))
                .andExpect(jsonPath("$.questionId", is(1)))
                .andExpect(jsonPath("$.result", is(QuestionResult.REPLY.getDescription())))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("questionId").description("????????? ?????? ????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("replyText").type(STRING).description("??????").attributes(field("constraints", "10 ~ 1000?????? ??????"))
                        ),
                        responseFields(
                                fieldWithPath("lectureId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("questionId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("result").type(STRING).description("API ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ??? ??????, ????????? ????????? ?????? ?????? ?????? ??????")
    void reply_answer_should_need_director_authorization() throws Exception {

        // given
        String body = createJson(createAnswerRequest());
        when(questionService.putAnswer(any(), any()))
                .thenThrow(new MemberException(MemberErrorCodes.NOT_LEADER_OR_DIRECTOR));

        // when, then
        mockMvc.perform(put(URL + "/{questionId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .characterEncoding("utf-8"))
                .andExpect(status().isForbidden());
    }

    private AnswerRequest createAnswerRequest() {
        return AnswerRequest.builder()
                .replyText("??????????????????????????????!!")
                .build();
    }

    private QuestionRequest createQuestionRequest() {
        return QuestionRequest.builder()
                .lectureId(1L)
                .questionForm(QuestionMainTextModifyRequest.builder()
                        .mainText("?????????")
                        .build())
                .build();
    }
}