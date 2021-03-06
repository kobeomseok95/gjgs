package com.gjgs.gjgs.modules.lecture.controllers;

import com.gjgs.gjgs.document.utils.DocumentLinkGenerator;
import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.coupon.dto.DirectorCouponResponse;
import com.gjgs.gjgs.modules.coupon.exception.CouponErrorCodes;
import com.gjgs.gjgs.modules.coupon.exception.CouponException;
import com.gjgs.gjgs.modules.coupon.services.DirectorCouponServiceImpl;
import com.gjgs.gjgs.modules.lecture.dtos.create.CreateLecture;
import com.gjgs.gjgs.modules.lecture.dtos.director.lecture.DirectorLectureResponse;
import com.gjgs.gjgs.modules.lecture.dtos.director.lecture.GetLectureType;
import com.gjgs.gjgs.modules.lecture.dtos.director.question.DirectorQuestionResponse;
import com.gjgs.gjgs.modules.lecture.dtos.director.schedule.DirectorLectureScheduleResponse;
import com.gjgs.gjgs.modules.lecture.dtos.director.schedule.DirectorScheduleResponse;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureException;
import com.gjgs.gjgs.modules.lecture.exceptions.ScheduleErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.ScheduleException;
import com.gjgs.gjgs.modules.lecture.services.director.lecture.DirectorLectureServiceImpl;
import com.gjgs.gjgs.modules.lecture.services.director.schedule.DirectorScheduleServiceImpl;
import com.gjgs.gjgs.modules.question.enums.QuestionStatus;
import com.gjgs.gjgs.modules.utils.exceptions.search.SearchValidator;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.generateLinkCode;
import static com.gjgs.gjgs.document.utils.RestDocsConfig.field;
import static com.gjgs.gjgs.modules.lecture.enums.ScheduleStatus.RECRUIT;
import static java.time.LocalDateTime.now;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = { DirectorLectureController.class, SearchValidator.class }
)
class DirectorLectureControllerTest extends RestDocsTestSupport {

    private final String LECTURE_URI = "/api/v1/mypage/directors/lectures";
    private final String SCHEDULE_URI = "/api/v1/mypage/directors/schedules";
    private final String QUESTION_URI = "/api/v1/mypage/directors/questions";
    private final String LECTURE_SCHEDULE_URI = "/api/v1/mypage/directors/lectures/{lectureId}/schedules";
    private final String SCHEDULE_DELETE_URI = "/api/v1/mypage/directors/lectures/{lectureId}/schedules/{scheduleId}";
    private final String COUPON_URI = "/api/v1/mypage/directors/coupons";

    @MockBean DirectorLectureServiceImpl directorLectureService;
    @MockBean DirectorScheduleServiceImpl directorScheduleService;
    @MockBean DirectorCouponServiceImpl directorCouponService;

    @BeforeEach
    void setUpMockUser() {
        securityDirectorMockSetting();
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ????????? ????????????")
    void get_director_lectures() throws Exception {

        // given
        GetLectureType all = GetLectureType.ALL;
        when(directorLectureService.getDirectorLectures(all))
                .thenReturn(getDirectorLectureResponse());

         // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(LECTURE_URI + "?condition=ALL")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestParameters(parameterWithName("condition").description(generateLinkCode(DocumentLinkGenerator.DocUrl.GET_LECTURE_TYPE))),
                        responseFields(
                                fieldWithPath("lectureList[].savedLectureId").type(NUMBER).description("?????? ?????? ????????? ID"),
                                fieldWithPath("lectureList[].thumbnailImageUrl").type(STRING).description("????????? ?????????"),
                                fieldWithPath("lectureList[].title").type(STRING).description("????????????"),
                                fieldWithPath("lectureList[].mainText").type(STRING).description("????????? ??????"),
                                fieldWithPath("lectureList[].lectureStatus").type(STRING).description("????????? ??????"),
                                fieldWithPath("lectureList[].finished").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("lectureList[].rejectReason").type(STRING).description("?????? ??????")
                        )
                ))
                ;
    }

    private DirectorLectureResponse getDirectorLectureResponse() {
        return DirectorLectureResponse.builder()
                .lectureList(List.of(DirectorLectureResponse.LectureResponse.builder()
                        .savedLectureId(1L)
                        .thumbnailImageUrl("test")
                        .title("test")
                        .mainText("test")
                        .lectureStatus("ACCEPT")
                        .finished(false)
                        .rejectReason("")
                        .build()
                )).build();
    }

    @Test
    @DisplayName("?????? ???????????? ??????????????? ????????? ????????????")
    void get_director_lectures_schedules() throws Exception {

        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(directorLectureService.getDirectorLecturesSchedules(any(), any()))
                .thenReturn(new PageImpl<>(getDirectorLectureScheduleResponse(), pageRequest, 1));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(SCHEDULE_URI + "?searchType=ALL")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("content[].lectureId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("content[].scheduleId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("content[].title").type(STRING).description("????????? ??????"),
                                fieldWithPath("content[].currentParticipants").type(NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("content[].maxParticipants").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("content[].scheduleDate").type(STRING).description("????????? ?????? ??????"),
                                fieldWithPath("content[].startHour").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("content[].startMinute").type(NUMBER).description("????????? ?????? ???"),
                                fieldWithPath("content[].endHour").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("content[].endMinute").type(NUMBER).description("????????? ?????? ???"),
                                fieldWithPath("content[].regularPrice").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("content[].priceOne").type(NUMBER).description("????????? 1??? ?????? ??????"),
                                fieldWithPath("content[].priceTwo").type(NUMBER).description("????????? 2??? ?????? ??????"),
                                fieldWithPath("content[].priceThree").type(NUMBER).description("????????? 3??? ?????? ??????"),
                                fieldWithPath("content[].priceFour").type(NUMBER).description("????????? 4??? ?????? ??????"),
                                fieldWithPath("content[].scheduleStatus").type(STRING).description("????????? ??????")
                        ).and(pageDescriptor())
                ));
    }

    private List<DirectorLectureScheduleResponse> getDirectorLectureScheduleResponse() {
        return List.of(
                DirectorLectureScheduleResponse.builder()
                        .lectureId(1L)
                        .scheduleId(1L)
                        .title("test")
                        .currentParticipants(2)
                        .maxParticipants(10)
                        .scheduleDate(LocalDate.now())
                        .startHour(12)
                        .startMinute(0)
                        .endHour(13)
                        .endMinute(0)
                        .regularPrice(1000)
                        .priceOne(1000)
                        .priceTwo(1000)
                        .priceThree(1000)
                        .priceFour(1000)
                        .scheduleStatus(RECRUIT.name())
                        .build()
        );
    }

    @Test
    @DisplayName("?????? ???????????? ??????????????? ????????? ???????????? / keyword??? ????????? ??????")
    void get_director_lectures_schedules_should_not_blank_keyword() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(SCHEDULE_URI + "?searchType=ALL&keyword=  ")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("SEARCH-400")));
    }

    @Test
    @DisplayName("?????? ???????????? ??????????????? ????????? ???????????? / ?????? ????????? yyyy-MM-dd??? ?????? ??????")
    void get_director_schedules_not_YYYYMMDD() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(SCHEDULE_URI + "?searchType=ALL&startDate=0101-23-45&endDate=0101-23-23")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }

    @Test
    @DisplayName("?????? ???????????? ??????????????? ????????? ????????????")
    void get_director_questions() throws Exception {

        // given
        List<DirectorQuestionResponse> content = createDirectorQuestionResponse();
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(directorLectureService.getDirectorQuestions(any(), any()))
                .thenReturn(new PageImpl<>(content, pageRequest, 1));

        // when, then
        mockMvc.perform(get(QUESTION_URI + "?questionStatus=COMPLETE&lectureId=1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestParameters(
                                parameterWithName("questionStatus").description(generateLinkCode(DocumentLinkGenerator.DocUrl.QUESTION_STATUS)),
                                parameterWithName("lectureId").description("???????????? ?????? ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("content[].lectureInfo.id").type(NUMBER).description("????????? ID"),
                                fieldWithPath("content[].lectureInfo.title").type(STRING).description("????????? ??????"),
                                fieldWithPath("content[].lectureInfo.lectureCreatedAt").type(STRING).description("???????????? ???????????? ??????"),
                                fieldWithPath("content[].questionInfo.id").type(NUMBER).description("????????? ID"),
                                fieldWithPath("content[].questionInfo.questionerId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("content[].questionInfo.questionerNickname").type(STRING).description("????????? ?????????"),
                                fieldWithPath("content[].questionInfo.questionMainText").type(STRING).description("???????????? ?????????"),
                                fieldWithPath("content[].questionInfo.questionAnswerText").type(STRING).description("???????????? ??????"),
                                fieldWithPath("content[].questionInfo.questionStatus").type(STRING).description("????????? ??????"),
                                fieldWithPath("content[].questionInfo.questionCreatedAt").type(STRING).description("????????? ?????? ??????")
                        ).and(pageDescriptor())
                ))
        ;
    }

    private List<DirectorQuestionResponse> createDirectorQuestionResponse() {
        return List.of(
                DirectorQuestionResponse.builder()
                        .lectureInfo(DirectorQuestionResponse.LectureInfo.builder()
                                .id(1L).title("test").lectureCreatedAt(now())
                                .build())
                        .questionInfo(DirectorQuestionResponse.QuestionInfo.builder()
                                .id(1L).questionerId(1L).questionerNickname("test").questionMainText("test")
                                .questionAnswerText("test").questionStatus(QuestionStatus.COMPLETE.name())
                                .questionCreatedAt(now())
                                .build())
                        .build()
        );
    }

    @Test
    @DisplayName("????????? ???????????? ???????????? ????????????.")
    void get_lecture_schedules() throws Exception {

        // given
        DirectorScheduleResponse response = createDirectorScheduleResponse();
        when(directorScheduleService.getLectureSchedules(any()))
                .thenReturn(response);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(LECTURE_SCHEDULE_URI, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("lectureId").description("???????????? ?????? ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("lectureId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("progressTime").type(NUMBER).description("????????? ?????? ???"),
                                fieldWithPath("minParticipants").type(NUMBER).description("????????? ?????? ?????? ??????"),
                                fieldWithPath("maxParticipants").type(NUMBER).description("????????? ?????? ?????? ??????"),
                                fieldWithPath("scheduleList[].scheduleId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("scheduleList[].lectureDate").type(STRING).description("????????? ?????? ??????"),
                                fieldWithPath("scheduleList[].startHour").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("scheduleList[].startMinute").type(NUMBER).description("????????? ?????? ???"),
                                fieldWithPath("scheduleList[].endHour").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("scheduleList[].endMinute").type(NUMBER).description("????????? ?????? ???"),
                                fieldWithPath("scheduleList[].currentParticipants").type(NUMBER).description("????????? ?????? ?????? ??????"),
                                fieldWithPath("scheduleList[].canDelete").type(BOOLEAN).description("??? ???????????? ?????? ??? ????????? ??????")
                        )
                ))
        ;
    }

    private DirectorScheduleResponse createDirectorScheduleResponse() {
        return DirectorScheduleResponse.builder()
                .lectureId(1L)
                .progressTime(60)
                .minParticipants(5)
                .maxParticipants(10)
                .scheduleList(Set.of(
                        DirectorScheduleResponse.ScheduleDto.builder()
                                .scheduleId(1L)
                                .lectureDate(LocalDate.now())
                                .startHour(12)
                                .startMinute(0)
                                .endHour(13)
                                .endMinute(0)
                                .currentParticipants(3)
                                .canDelete(false)
                                .build()
                ))
                .build();
    }

    @Test
    @DisplayName("???????????? ????????????. / ?????? ????????? ????????? ?????? ??????")
    void add_schedule_should_follow_constraint() throws Exception {

        // given
        CreateLecture.ScheduleDto schedule = createScheduleDtoNotConstraint();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(LECTURE_SCHEDULE_URI, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(schedule))
                .contentType(APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(6)));
    }

    @Test
    @DisplayName("???????????? ????????????.")
    void add_schedule() throws Exception {

        // given
        CreateLecture.ScheduleDto schedule = createScheduleDto();
        when(directorScheduleService.createSchedule(any(), any()))
                .thenReturn(DirectorScheduleResponse.PostDelete.builder()
                        .scheduleId(1L).result(DirectorScheduleResponse.PostDelete.Result.CREATE)
                        .build());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(LECTURE_SCHEDULE_URI, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(schedule))
                .contentType(APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("lectureId").description("???????????? ????????? ????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("progressMinute").type(NUMBER).description("???????????? ???????????? ??????").attributes(field("constraints", "?????? 60 ??????, 30??? ????????? ???????????? ???")),
                                fieldWithPath("lectureDate").type(STRING).description("?????? ??????").attributes(field("constraints", "yyyy-MM-dd ????????? ????????? ???")),
                                fieldWithPath("startHour").type(NUMBER).description("????????? ?????? ??????").attributes(field("constraints", "0 ~ 23 ?????? ?????? ??????")),
                                fieldWithPath("startMinute").type(NUMBER).description("????????? ?????? ???").attributes(field("constraints", "0 ~ 59 ?????? ?????? ??????, 30??? ????????? ???????????? ???")),
                                fieldWithPath("endHour").type(NUMBER).description("?????? ????????? ?????? ??????").optional(),
                                fieldWithPath("endMinute").type(NUMBER).description("?????? ????????? ?????? ??????").optional()
                        ),
                        responseFields(
                                fieldWithPath("scheduleId").type(NUMBER).description("??????????????? ????????? ????????? ID"),
                                fieldWithPath("result").type(STRING).description(generateLinkCode(DocumentLinkGenerator.DocUrl.SCHEDULE_RESULT))
                        )
                ))
        ;
    }

    @Test
    @DisplayName("???????????? ????????????. / ?????? ???????????? ???????????? ?????? ?????? ????????? ??? ??????.")
    void delete_schedule_should_not_exist_participants() throws Exception {

        // given
        stubbingScheduleInParticipants();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(SCHEDULE_DELETE_URI, 1, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("UTF-8"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("???????????? ????????????.")
    void delete_schedule() throws Exception {

        // given
        DirectorScheduleResponse.PostDelete response = DirectorScheduleResponse.PostDelete.delete(1L);
        when(directorScheduleService.deleteSchedule(any(), any()))
                .thenReturn(response);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(SCHEDULE_DELETE_URI, 1, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("lectureId").description("???????????? ?????? ????????? ID"),
                                parameterWithName("scheduleId").description("?????? ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("scheduleId").description("????????? ????????? ID"),
                                fieldWithPath("result").description(generateLinkCode(DocumentLinkGenerator.DocUrl.SCHEDULE_RESULT))
                        )))
        ;
    }

    @Test
    @DisplayName("?????? ?????? ???????????? ?????? ????????????")
    void get_director_coupon() throws Exception {

        // given
        DirectorCouponResponse response = createDirectorCouponResponse();
        when(directorCouponService.getDirectorCoupons())
                .thenReturn(response);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(COUPON_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("couponResponseList[].lectureId").type(NUMBER).description("????????? ????????? ID"),
                                fieldWithPath("couponResponseList[].title").type(STRING).description("????????? ??????"),
                                fieldWithPath("couponResponseList[].issueDate").type(STRING).description("????????? ??????"),
                                fieldWithPath("couponResponseList[].closeDate").type(STRING).description("???????????? ????????? ???????????? ?????? ??????"),
                                fieldWithPath("couponResponseList[].discountPrice").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("couponResponseList[].chargeCount").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("couponResponseList[].receivePeople").type(NUMBER).description("?????? ????????? ???"),
                                fieldWithPath("couponResponseList[].remainCount").type(NUMBER).description("?????? ?????? ???")
                        )
                ))
        ;
    }

    private DirectorCouponResponse createDirectorCouponResponse() {
        return DirectorCouponResponse.builder()
                .couponResponseList(List.of(DirectorCouponResponse.CouponResponse.builder()
                        .lectureId(1L)
                        .title("test")
                        .issueDate(LocalDateTime.now())
                        .closeDate(LocalDateTime.now().plusDays(30))
                        .discountPrice(1000)
                        .chargeCount(10)
                        .receivePeople(1)
                        .remainCount(1)
                        .build()
                ))
                .build();
    }

    @Test
    @DisplayName("?????? (???)????????????")
    void issue_coupon() throws Exception {

        // given
        CreateLecture.CouponDto request = CreateLecture.CouponDto.builder()
                .couponPrice(1000).couponCount(10)
                .build();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(COUPON_URI + "/{lectureId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(APPLICATION_JSON)
                .content(createJson(request)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("lectureId").description("????????? (???)?????? ??? ????????? ID")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("?????? (???)???????????? / ?????? ???????????? ????????? ???????????? ??????")
    void issue_coupon_should_not_remain_coupon() throws Exception {

        // given
        stubbingCouponNotIssueException();
        CreateLecture.CouponDto request = CreateLecture.CouponDto.builder()
                .couponPrice(1000).couponCount(10)
                .build();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(COUPON_URI + "/{lectureId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(APPLICATION_JSON)
                .content(createJson(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("?????? ?????? ????????????")
    void stop_issue_coupon() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(COUPON_URI + "/{lectureId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("lectureId").description("?????? ????????? ????????? ????????? ID")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("?????? ?????? ??? ????????? ????????????")
    void delete_reject_lecture() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(LECTURE_URI + "/{lectureId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("lectureId").description("????????? ????????? ID")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("?????? ?????? ??? ????????? ???????????? / reject??? ???????????? ?????? ??????")
    void delete_reject_lecture_not_found() throws Exception {

        // given
        stubbingNotRejectLecture();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(LECTURE_URI + "/{lectureId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("?????? ?????? ??? ????????? ???????????????")
    void rewrite_lecture() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(LECTURE_URI + "/{lectureId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("lectureId").description("????????? ??? ????????? ID")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("?????? ?????? ??? ????????? ??????????????? / ??? ???, ???????????? ???????????? ????????? ?????????.")
    void rewrite_lecture_should_not_exist_creating_lecture() throws Exception {

        // given
        stubbingExistCreatingLectureException();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(LECTURE_URI + "/{lectureId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isConflict());
    }

    private void stubbingNotRejectLecture() {
        doThrow(new LectureException(LectureErrorCodes.NOT_REJECT_LECTURE)).when(directorLectureService).deleteRejectLecture(any());
    }

    private void stubbingScheduleInParticipants() {
        when(directorScheduleService.deleteSchedule(any(), any()))
                .thenThrow(new ScheduleException(ScheduleErrorCodes.SCHEDULE_NOT_DELETE));
    }

    private void stubbingExistCreatingLectureException() {
        doThrow(new LectureException(LectureErrorCodes.EXIST_CREATING_LECTURE)).when(directorLectureService).changeLectureCreating(any());
    }

    private void stubbingCouponNotIssueException() {
        doThrow(new CouponException(CouponErrorCodes.AVAILABLE_COUPON)).when(directorCouponService).issue(any(), any());
    }

    private CreateLecture.ScheduleDto createScheduleDtoNotConstraint() {
        return CreateLecture.ScheduleDto.builder()
                .progressMinute(59)
                .startHour(24)
                .startMinute(61)
                .build();
    }

    private CreateLecture.ScheduleDto createScheduleDto() {
        return CreateLecture.ScheduleDto.builder()
                .progressMinute(120)
                .startHour(17)
                .startMinute(30)
                .lectureDate(LocalDate.of(2021, 9, 15))
                .build();
    }
}
