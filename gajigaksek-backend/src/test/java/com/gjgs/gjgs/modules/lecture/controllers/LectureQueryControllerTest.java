package com.gjgs.gjgs.modules.lecture.controllers;

import com.gjgs.gjgs.document.utils.DocumentLinkGenerator;
import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.bulletin.dto.search.BulletinSearchResponse;
import com.gjgs.gjgs.modules.dummy.LectureDtoDummy;
import com.gjgs.gjgs.modules.lecture.dtos.LectureDetailResponse;
import com.gjgs.gjgs.modules.lecture.dtos.LectureQuestionsResponse;
import com.gjgs.gjgs.modules.lecture.dtos.review.ReviewResponse;
import com.gjgs.gjgs.modules.lecture.dtos.search.LectureSearchResponse;
import com.gjgs.gjgs.modules.lecture.services.LectureServiceImpl;
import com.gjgs.gjgs.modules.team.enums.Age;
import com.gjgs.gjgs.modules.utils.exceptions.search.SearchValidator;
import com.gjgs.gjgs.modules.utils.querydsl.RepositorySliceHelper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.generateLinkCode;
import static org.elasticsearch.rest.RestStatus.BAD_REQUEST;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(
        value = {LectureQueryController.class, SearchValidator.class}
)
class LectureQueryControllerTest extends RestDocsTestSupport {

    @MockBean LectureServiceImpl lectureService;

    @Test
    @DisplayName("????????? ??????")
    void get_lectures() throws Exception {

        // given
        when(lectureService.searchLectures(any(), any()))
                .thenReturn(createSearchResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/lectures?categoryIdList=1,2,3&keyword=test&zoneId=1&searchPriceCondition=LOWER_EQUAL_FIVE&createdDate,desc&reviewCount,desc&score,desc&clickCount,desc")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.pageable.offset", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(10)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.first", is(true)))
                .andDo(restDocs.document(
                        requestParameters(
                                parameterWithName("searchPriceCondition").description(generateLinkCode(DocumentLinkGenerator.DocUrl.SEARCH_PRICE_CONDITION)),
                                parameterWithName("categoryIdList").description("????????? ???????????? ID ?????? ?????? ??????"),
                                parameterWithName("keyword").description("?????????, ?????? ?????????"),
                                parameterWithName("zoneId").description("?????? ID, ?????? ??????"),
                                parameterWithName("createdDate,desc").description("?????????"),
                                parameterWithName("score,desc").description("?????? ?????? ???"),
                                parameterWithName("reviewCount,desc").description("?????? ?????? ???"),
                                parameterWithName("clickCount,desc").description("?????????")
                        ),
                        responseFields(
                                fieldWithPath("content[].lectureId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("content[].onlyGjgs").type(BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("content[].myFavorite").type(BOOLEAN).description("?????? ??? ????????? ????????? ??????"),
                                fieldWithPath("content[].imageUrl").type(STRING).description("?????? URL"),
                                fieldWithPath("content[].title").type(STRING).description("????????? ??????"),
                                fieldWithPath("content[].zoneId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("content[].priceOne").type(NUMBER).description("????????? 1??? ?????? ??????"),
                                fieldWithPath("content[].priceTwo").type(NUMBER).description("????????? 2??? ?????? ??????"),
                                fieldWithPath("content[].priceThree").type(NUMBER).description("????????? 3??? ?????? ??????"),
                                fieldWithPath("content[].priceFour").type(NUMBER).description("????????? 4??? ?????? ??????")
                        ).and(pageDescriptor())
                ));
    }

    @Test
    @DisplayName("?????? ?????????(?????? ?????????, ????????? ?????? ????????? ??????)")
    void get_recommend_lectures() throws Exception {

        // given
        when(lectureService.searchLectures(any(), any()))
                .thenReturn(createRecommendSearchResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/lectures?categoryIdList=1,2,3&reviewCount,desc&score,desc&clickCount,desc&createdDate,desc")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.first", is(true)))
                .andDo(restDocs.document(
                        requestParameters(
                                parameterWithName("categoryIdList").description("???????????? ????????? ????????????(????????? ?????? ?????????, ????????? ????????? ?????? ?????? ?????????)"),
                                parameterWithName("score,desc").description("?????? ?????? ???"),
                                parameterWithName("clickCount,desc").description("?????????"),
                                parameterWithName("reviewCount,desc").description("?????? ?????? ???"),
                                parameterWithName("createdDate,desc").description("?????????")
                        ),
                        responseFields(
                                fieldWithPath("content[].lectureId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("content[].onlyGjgs").type(BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("content[].myFavorite").type(BOOLEAN).description("?????? ??? ????????? ????????? ??????"),
                                fieldWithPath("content[].imageUrl").type(STRING).description("?????? URL"),
                                fieldWithPath("content[].title").type(STRING).description("????????? ??????"),
                                fieldWithPath("content[].zoneId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("content[].priceOne").type(NUMBER).description("????????? 1??? ?????? ??????"),
                                fieldWithPath("content[].priceTwo").type(NUMBER).description("????????? 2??? ?????? ??????"),
                                fieldWithPath("content[].priceThree").type(NUMBER).description("????????? 3??? ?????? ??????"),
                                fieldWithPath("content[].priceFour").type(NUMBER).description("????????? 4??? ?????? ??????")
                        ).and(pageDescriptor())
                ));
    }

    private Page<LectureSearchResponse> createRecommendSearchResponse() {
        return new PageImpl<>(createSearchResponseResult(),
                PageRequest.of(0, 4), 1);
    }


    @Test
    @DisplayName("????????? ?????? / ???????????? ????????? ????????? ?????? ??????")
    void get_lectures_should_run_elasticsearch() throws Exception {

        // given
        stubbingElasticsearchException();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/lectures?categoryIdList=1,2,3&keyword=test&zoneId=1&searchPriceCondition=LOWER_EQUAL_FIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest());
    }

    private void stubbingElasticsearchException() {
        when(lectureService.searchLectures(any(), any())).thenThrow(new ElasticsearchStatusException("?????? ?????????, ????????? ????????? ????????????.", BAD_REQUEST));
    }

    @Test
    @DisplayName("????????? ?????? / keyword??? ????????? ??????")
    void get_lectures_should_not_keyword_is_blank() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/lectures?keyword=      ")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void get_lecture() throws Exception {

        // when
        LectureDetailResponse res = LectureDtoDummy.createLectureDetailResponse();
        when(lectureService.getLecture(any()))
                .thenReturn(res);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/lectures/{lectureId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.lectureId", is(res.getLectureId().intValue())))
                .andExpect(jsonPath("$.thumbnailImageUrl", is(res.getThumbnailImageUrl())))
                .andExpect(jsonPath("$.lectureTitle", is(res.getLectureTitle())))
                .andExpect(jsonPath("$.zoneId", is(res.getZoneId().intValue())))
                .andExpect(jsonPath("$.categoryId", is(res.getCategoryId().intValue())))
                .andExpect(jsonPath("$.progressTime", is(res.getProgressTime())))
                .andExpect(jsonPath("$.priceOne", is(res.getPriceOne())))
                .andExpect(jsonPath("$.priceTwo", is(res.getPriceTwo())))
                .andExpect(jsonPath("$.priceThree", is(res.getPriceThree())))
                .andExpect(jsonPath("$.priceFour", is(res.getPriceFour())))
                .andExpect(jsonPath("$.regularPrice", is(res.getRegularPrice())))
                .andExpect(jsonPath("$.mainText", is(res.getMainText())))
                .andExpect(jsonPath("$.directorResponse.directorId", is(res.getDirectorResponse().getDirectorId().intValue())))
                .andExpect(jsonPath("$.directorResponse.directorProfileText", is(res.getDirectorResponse().getDirectorProfileText())))
                .andExpect(jsonPath("$.directorResponse.directorProfileImageUrl", is(res.getDirectorResponse().getDirectorProfileImageUrl())))
                .andExpect(jsonPath("$.curriculumResponseList", hasSize(res.getCurriculumResponseList().size())))
                .andExpect(jsonPath("$.finishedProductResponseList", hasSize(res.getFinishedProductResponseList().size())))
                .andExpect(jsonPath("$.scheduleResponseList", hasSize(res.getScheduleResponseList().size())))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("lectureId").description("????????? ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("lectureId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("thumbnailImageUrl").type(STRING).description("????????? URL"),
                                fieldWithPath("lectureTitle").type(STRING).description("????????? ??????"),
                                fieldWithPath("zoneId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("categoryId").type(NUMBER).description("?????? ???????????? ID"),
                                fieldWithPath("progressTime").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("priceOne").type(NUMBER).description("????????? 1??? ?????? ??????"),
                                fieldWithPath("priceTwo").type(NUMBER).description("????????? 2??? ?????? ??????"),
                                fieldWithPath("priceThree").type(NUMBER).description("????????? 3??? ?????? ??????"),
                                fieldWithPath("priceFour").type(NUMBER).description("????????? 4??? ?????? ??????"),
                                fieldWithPath("regularPrice").type(NUMBER).description("????????? ?????? ?????? ??????"),
                                fieldWithPath("mainText").type(STRING).description("????????? ?????? ??????"),
                                fieldWithPath("myFavorite").type(BOOLEAN).description("?????? ????????? ????????? ??????"),
                                fieldWithPath("minParticipants").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("maxParticipants").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("directorResponse.directorId").type(NUMBER).description("???????????? ID"),
                                fieldWithPath("directorResponse.directorProfileText").type(STRING).description("???????????? ????????? ??????"),
                                fieldWithPath("directorResponse.directorProfileImageUrl").type(STRING).description("???????????? ????????? ?????? URL"),
                                fieldWithPath("curriculumResponseList[].curriculumId").type(NUMBER).description("???????????? ID"),
                                fieldWithPath("curriculumResponseList[].curriculumImageUrl").type(STRING).description("???????????? ????????? URL"),
                                fieldWithPath("curriculumResponseList[].order").type(NUMBER).description("??????????????? ??????"),
                                fieldWithPath("curriculumResponseList[].title").type(STRING).description("???????????? ?????????"),
                                fieldWithPath("curriculumResponseList[].detailText").type(STRING).description("???????????? ??????"),
                                fieldWithPath("finishedProductResponseList[].finishedProductId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("finishedProductResponseList[].order").type(NUMBER).description("???????????? ??????"),
                                fieldWithPath("finishedProductResponseList[].finishedProductImageUrl").type(STRING).description("???????????? ????????? URL"),
                                fieldWithPath("finishedProductResponseList[].text").type(STRING).description("????????? ??????"),
                                fieldWithPath("scheduleResponseList[].scheduleId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("scheduleResponseList[].lectureDate").type(STRING).description("????????? ?????? ??????"),
                                fieldWithPath("scheduleResponseList[].currentParticipants").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("scheduleResponseList[].startHour").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("scheduleResponseList[].startMinute").type(NUMBER).description("????????? ?????? ???"),
                                fieldWithPath("scheduleResponseList[].endHour").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("scheduleResponseList[].endMinute").type(NUMBER).description("????????? ?????? ???"),
                                fieldWithPath("scheduleResponseList[].progressMinutes").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("scheduleResponseList[].scheduleStatus").type(STRING).description("???????????? ??????")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("?????? ???????????? ????????? ???????????? ????????????")
    void get_lecture_favorite_bulletins() throws Exception {

        // given
        when(lectureService.getBulletinsPickedLecture(any(), any()))
                .thenReturn(createBulletinsPickedLectureResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/lectures/{lectureId}/bulletins", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.pageable.offset", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(10)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.first", is(true)))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("lectureId").description("???????????? ?????? ????????????????????? ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("content[].bulletinId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("content[].lectureImageUrl").type(STRING).description("????????? ?????????"),
                                fieldWithPath("content[].zoneId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("content[].categoryId").type(NUMBER).description("?????? ???????????? ID"),
                                fieldWithPath("content[].bulletinTitle").type(STRING).description("????????? ??????"),
                                fieldWithPath("content[].age").type(STRING).description("????????? ?????????"),
                                fieldWithPath("content[].time").type(STRING).description("????????? ?????? ??????"),
                                fieldWithPath("content[].myFavorite").type(BOOLEAN).description("?????? ????????? ????????? ??????"),
                                fieldWithPath("content[].nowMembers").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("content[].maxMembers").type(NUMBER).description("?????? ?????? ??????")
                        ).and(pageDescriptor())
                ));
    }

    @Test
    @DisplayName("???????????? ????????? ??????")
    void get_lecture_questions() throws Exception {

        // given
        when(lectureService.getLectureQuestions(any(), any()))
                .thenReturn(createLectureQuestions());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/lectures/{lectureId}/questions", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("lectureId").description("???????????? ????????? ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("content[].questionId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("content[].questionerId").type(NUMBER).description("????????? ????????? ID"),
                                fieldWithPath("content[].questionerNickname").type(STRING).description("????????? ????????? ?????????"),
                                fieldWithPath("content[].questionerProfileImageUrl").type(STRING).description("????????? ????????? ????????? ?????? URL"),
                                fieldWithPath("content[].questionDate").type(STRING).description("?????? ??????"),
                                fieldWithPath("content[].questionText").type(STRING).description("?????? ??????"),
                                fieldWithPath("content[].answerComplete").type(BOOLEAN).description("???????????? ?????? ??????")
                        ).and(pageDescriptor())
                ));
    }

    @Test
    @DisplayName("????????? ?????? ?????? ??????(?????????)")
    void get_lecture_reviews() throws Exception {

        // given
        when(lectureService.getLectureReviews(any(), any()))
                .thenReturn(createLectureReviews());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/lectures/{lectureId}/reviews", 1))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(parameterWithName("lectureId").description("????????? ????????? ????????? ID")),
                        responseFields(
                                fieldWithPath("content[].reviewId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("content[].reviewImageFileUrl").type(STRING).description("????????? ?????? ?????? ??????").optional(),
                                fieldWithPath("content[].text").type(STRING).description("????????? ??????"),
                                fieldWithPath("content[].replyText").type(STRING).description("???????????? ??????"),
                                fieldWithPath("content[].score").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("content[].nickname").type(STRING).description("?????? ?????????"),
                                fieldWithPath("content[].profileImageFileUrl").type(STRING).description("????????? ????????? ????????? URL")
                        ).and(pageDescriptor())
                ));
    }

    @Test
    @DisplayName("???????????? ???????????? ????????? ??????")
    void get_director_lectures() throws Exception {

        // given
        when(lectureService.getDirectorLectures(any(), any()))
                .thenReturn(createDirectorLectures());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/lectures/directors/{directorId}", 1))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("directorId").description("????????? ???????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("content[].lectureId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("content[].onlyGjgs").type(BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("content[].myFavorite").type(BOOLEAN).description("?????? ??? ????????? ????????? ??????"),
                                fieldWithPath("content[].imageUrl").type(STRING).description("?????? URL"),
                                fieldWithPath("content[].title").type(STRING).description("????????? ??????"),
                                fieldWithPath("content[].zoneId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("content[].priceOne").type(NUMBER).description("????????? 1??? ?????? ??????"),
                                fieldWithPath("content[].priceTwo").type(NUMBER).description("????????? 2??? ?????? ??????"),
                                fieldWithPath("content[].priceThree").type(NUMBER).description("????????? 3??? ?????? ??????"),
                                fieldWithPath("content[].priceFour").type(NUMBER).description("????????? 4??? ?????? ??????")
                        ).and(sliceDescriptor())
                ));
    }

    private Slice<LectureSearchResponse> createDirectorLectures() {
        return RepositorySliceHelper.toSlice(createSearchResponseResult(), PageRequest.of(0, 20));
    }

    private Page<ReviewResponse> createLectureReviews() {
        List<ReviewResponse> content = List.of(
                ReviewResponse.builder()
                        .reviewId(1L)
                        .reviewImageFileUrl("test")
                        .text("test")
                        .replyText("test")
                        .score(4)
                        .nickname("test")
                        .profileImageFileUrl("test")
                        .build()
        );
        return new PageImpl<>(content, PageRequest.of(0, 10), 1);
    }

    private Page<LectureQuestionsResponse> createLectureQuestions() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<LectureQuestionsResponse> content = List.of(
                createLectureQuestionResponse()
        );
        return new PageImpl<>(content, pageRequest, 1);
    }

    private LectureQuestionsResponse createLectureQuestionResponse() {
        return LectureQuestionsResponse.builder()
                .questionId(1L)
                .questionerId(1L)
                .questionerNickname("?????????1")
                .questionerProfileImageUrl("url")
                .questionDate(LocalDateTime.now())
                .questionText("text")
                .answerComplete(false)
                .build();
    }

    private Page<BulletinSearchResponse> createBulletinsPickedLectureResponse() {
        return new PageImpl<>(createBulletinsPickedLectureResponseResult(),
                PageRequest.of(0, 10), 1);
    }

    private List<BulletinSearchResponse> createBulletinsPickedLectureResponseResult() {
        List<BulletinSearchResponse> list = new ArrayList<>();
        list.add(
                BulletinSearchResponse.builder()
                        .bulletinId((long) 1)
                        .lectureImageUrl("test")
                        .zoneId((long) 1)
                        .categoryId((long) 1)
                        .bulletinTitle("test")
                        .age(Age.TWENTYFIVE_TO_THIRTY.name())
                        .time("MORNING")
                        .myFavorite(false)
                        .nowMembers(2)
                        .maxMembers(4)
                        .build());
        return list;
    }

    private Page<LectureSearchResponse> createSearchResponse() {
        return new PageImpl<>(createSearchResponseResult(),
                PageRequest.of(0, 10), 1);
    }

    private List<LectureSearchResponse> createSearchResponseResult() {
        List<LectureSearchResponse> list = new ArrayList<>();
        list.add(
            LectureSearchResponse.builder()
                    .lectureId((long) 1)
                    .isOnlyGjgs(false)
                    .imageUrl("test")
                    .title("test")
                    .zoneId(1L)
                    .priceOne(100)
                    .priceTwo(100)
                    .priceThree(100)
                    .priceFour(100)
                    .build());
        return list;
    }
}