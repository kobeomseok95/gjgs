package com.gjgs.gjgs.modules.lecture.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.lecture.dtos.create.CreateLecture;
import com.gjgs.gjgs.modules.lecture.dtos.create.CreateLectureProcessResponse;
import com.gjgs.gjgs.modules.lecture.dtos.create.TemporaryStorageLectureManageResponse;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureException;
import com.gjgs.gjgs.modules.lecture.services.temporaryStore.manage.TemporaryStorageLectureManageServiceImpl;
import com.gjgs.gjgs.modules.lecture.services.temporaryStore.put.*;
import com.gjgs.gjgs.modules.lecture.validators.minute.MinuteValidator;
import com.gjgs.gjgs.modules.utils.exceptions.FileErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.DocUrl.CREATE_LECTURE_STEP;
import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.DocUrl.LECTURE_SAVE_DELETE_RESPONSE;
import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.generateLinkCode;
import static com.gjgs.gjgs.document.utils.RestDocsConfig.field;
import static com.gjgs.gjgs.modules.lecture.dtos.create.CreateLectureStep.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(
        value = { TemporaryStorageLectureController.class , MinuteValidator.class }
)
class TemporaryStorageLectureControllerTest extends RestDocsTestSupport {

    private final String PUT = "PUT";
    private final String URL = "/api/v2/director/lectures";

    @BeforeEach
    void setUpMockUser() {
        securityDirectorMockSetting();
    }

    @MockBean PutLectureServiceFactory factory;
    @MockBean PutCurriculumServiceImpl putCurriculumService;
    @MockBean PutIntroServiceImpl putIntroService;
    @MockBean PutFirstServiceImpl putFirstService;
    @MockBean PutScheduleServiceImpl putScheduleService;
    @MockBean PutPriceCouponServiceImpl putPriceCouponService;
    @MockBean TemporaryStorageLectureManageServiceImpl temporaryStorageLectureManageService;

    @Test
    @DisplayName("?????? ?????? 1, ???????????? ????????? ???????????? ??????")
    void common_exception_should_thumbnail_is_one() throws Exception {

        // given
        CreateLecture.FirstRequest dto = CreateLecture.FirstRequest.builder()
                .createLectureStep(FIRST).categoryId(1L)
                .address("????????? ?????????").title("??????????????? ??????????????? ???????????????!").zoneId(1L).build();
        MockMultipartFile firstRequest = getMockMultipartJson(dto);
        MockMultipartFile thumbnailImageFileUrl = getMockMultipartFile();
        when(factory.getService(any())).thenReturn(putFirstService);
        when(putFirstService.putLectureProcess(any(), any()))
                .thenThrow(new LectureException(LectureErrorCodes.THUMBNAIL_IS_NOT_ONE));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(firstRequest)
                .file(thumbnailImageFileUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("?????? ?????? 2, ????????? ?????? ?????? ????????? ??????")
    void common_exception_should_not_file_missing() throws Exception {

        // given
        CreateLecture.FirstRequest dto = CreateLecture.FirstRequest.builder()
                .createLectureStep(FIRST).categoryId(1L)
                .address("????????? ?????????").title("??????????????? ??????????????? ???????????????!").zoneId(1L).build();
        MockMultipartFile firstRequest = getMockMultipartJson(dto);
        MockMultipartFile thumbnailImageFileUrl = getMockMultipartFile();
        when(factory.getService(any())).thenReturn(putFirstService);
        when(putFirstService.putLectureProcess(any(), any()))
                .thenThrow(new LectureException(FileErrorCodes.MISSING_FILE));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(firstRequest)
                .file(thumbnailImageFileUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("?????? ?????? 3, ?????? ???????????? ?????? ??????")
    void common_exception_should_equal_file_size_and_contents_size() throws Exception {

        // given
        CreateLecture.FirstRequest dto = CreateLecture.FirstRequest.builder()
                .createLectureStep(FIRST).categoryId(1L)
                .address("????????? ?????????").title("??????????????? ??????????????? ???????????????!").zoneId(1L).build();
        MockMultipartFile firstRequest = getMockMultipartJson(dto);
        MockMultipartFile thumbnailImageFileUrl = getMockMultipartFile();
        when(factory.getService(any())).thenReturn(putFirstService);
        when(putFirstService.putLectureProcess(any(), any()))
                .thenThrow(new LectureException(LectureErrorCodes.PRODUCT_AND_FILE_NOT_EQUAL));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(firstRequest)
                .file(thumbnailImageFileUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("????????? ?????? ????????????, ?????? ?????? / ??????, ?????? ??????")
    void save_lecture_first() throws Exception {

        // given
        CreateLecture.FirstRequest dto = CreateLecture.FirstRequest.builder()
                .createLectureStep(FIRST).categoryId(1L)
                .address("????????? ?????????").title("??????????????? ??????????????? ???????????????!").zoneId(1L).build();
        MockMultipartFile firstRequest = getMockMultipartJson(dto);
        MockMultipartFile thumbnailImageFileUrl = getMockMultipartFile();
        when(factory.getService(any())).thenReturn(putFirstService);
        when(putFirstService.putLectureProcess(any(), any()))
                .thenReturn(CreateLectureProcessResponse.completeSaveFirst(1L));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(firstRequest)
                .file(thumbnailImageFileUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestParts(
                                partWithName("request").description("???????????? ?????? ????????? ?????? JSON"),
                                partWithName("files").description("????????? ?????????").attributes(field("constraints", "????????? ??????"))
                        ),
                        requestPartFields("request",
                                fieldWithPath("createLectureStep").type(STRING).description(generateLinkCode(CREATE_LECTURE_STEP)),
                                fieldWithPath("lectureId").optional().type(NUMBER).description("????????? ID (????????? ???????????? ????????? ?????? ??????)").attributes(field("constraints","nullable")),
                                fieldWithPath("categoryId").type(NUMBER).description("?????? ???????????? ID").attributes(field("constraints","NOT NULL")),
                                fieldWithPath("zoneId").type(NUMBER).description("????????? ?????? ID").attributes(field("constraints","NOT NULL")),
                                fieldWithPath("title").type(STRING).description("????????? ??????").attributes(field("constraints","10??? ??????, 100??? ??????")),
                                fieldWithPath("address").type(STRING).description("???????????? ???????????? ??????").attributes(field("constraint", "NOT NULL")),
                                fieldWithPath("thumbnailImageFileName").optional().description("?????? ?????? ???").attributes(field("constraint", "NULL")),
                                fieldWithPath("thumbnailImageFileUrl").optional().description("?????? ?????? ???").attributes(field("constraint", "NULL"))
                        ),
                        responseFields(
                                fieldWithPath("lectureId").description("???????????? ????????? ????????? ID"),
                                fieldWithPath("completedStepName").description(generateLinkCode(CREATE_LECTURE_STEP))
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ????????????, ??????, zoneId, ????????? ?????? ??????")
    void save_lecture_first_should_follow_constraints() throws Exception {

        // given
        CreateLecture.FirstRequest dto = CreateLecture.FirstRequest.builder()
                .createLectureStep(FIRST).build();
        MockMultipartFile firstRequest = getMockMultipartJson(dto);
        MockMultipartFile thumbnailImageFileUrl = getMockMultipartFile();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(firstRequest)
                .file(thumbnailImageFileUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                })
                .contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(4)));
    }

    @Test
    @DisplayName("????????? ????????? 10??? ????????? ??????")
    void save_lecture_first_should_lecture_title_over_10_length() throws Exception {

        // given
        CreateLecture.FirstRequest dto = CreateLecture.FirstRequest.builder().createLectureStep(FIRST)
                .categoryId(1L).address("????????? ?????????").title("gg").zoneId(1L).build();
        MockMultipartFile firstRequest = getMockMultipartJson(dto);
        MockMultipartFile thumbnailImageFileUrl = getMockMultipartFile();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(firstRequest)
                .file(thumbnailImageFileUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @Test
    @DisplayName("????????? ?????? ????????????")
    void save_lecture_intro() throws Exception {

        // given
        CreateLecture.IntroRequest dto = createIntro();
        MockMultipartFile intro = getMockMultipartJson(dto);
        List<MockMultipartFile> files = getMockMultipartFileList(3);
        when(factory.getService(any())).thenReturn(putIntroService);
        when(putIntroService.putLectureProcess(any(), any()))
                .thenReturn(CreateLectureProcessResponse.completeIntro(1L));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(intro)
                .file(files.get(0))
                .file(files.get(1))
                .file(files.get(2))
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestParts(
                                partWithName("request").description("???????????? ?????? ????????? ?????? JSON"),
                                partWithName("files").description("????????? ????????? ?????? ?????? ??????").attributes(field("constraints", "1 ~ 4??? ?????? ??????, finishedProductInfoList size??? ?????? ?????? ?????? ?????? ??????"))
                        ),
                        requestPartFields("request",
                                fieldWithPath("mainText").type(STRING).description("????????? ?????????").attributes(field("constraints", "10??? ?????? 100??? ??????")),
                                fieldWithPath("createLectureStep").type(STRING).description(generateLinkCode(CREATE_LECTURE_STEP)),
                                fieldWithPath("finishedProductInfoList[]").description("????????? ?????????").attributes(field("constraints", "????????? ????????? 1????????? 4??? ????????? ??????")),
                                fieldWithPath("finishedProductInfoList[].order").type(NUMBER).description("????????? ????????? ??????"),
                                fieldWithPath("finishedProductInfoList[].text").type(STRING).description("????????? ????????? ?????????"),
                                fieldWithPath("finishedProductInfoList[].finishedProductImageName").description("?????? ????????? ?????? ??????"),
                                fieldWithPath("finishedProductInfoList[].finishedProductImageUrl").description("?????? ????????? ?????? ??????")
                                ),
                        responseFields(
                                fieldWithPath("lectureId").description("???????????? ????????? ????????? ID"),
                                fieldWithPath("completedStepName").description(generateLinkCode(CREATE_LECTURE_STEP))
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ???????????? / ????????? ?????????, ?????? ?????? ????????? ?????? ??????")
    void save_lecture_intro_should_follow_constraints() throws Exception {

        // given
        CreateLecture.IntroRequest dto = CreateLecture.IntroRequest.builder().createLectureStep(INTRO).build();
        MockMultipartFile intro = getMockMultipartJson(dto);
        List<MockMultipartFile> files = getMockMultipartFileList(3);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(intro)
                .file(files.get(0))
                .file(files.get(1))
                .file(files.get(2))
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(3)));
    }

    @Test
    @DisplayName("????????? ?????? ???????????? / ????????? ???????????? ????????? ????????? ???????????? ????????? ????????? ???????????? 10??? ????????? ??????")
    void save_lecture_intro_should_main_text_over_10_length_and_not_input_order_text() throws Exception {

        // given
        CreateLecture.IntroRequest dto = createEmptyOrderTextListAndNotOver10MainText();
        MockMultipartFile intro = getMockMultipartJson(dto);
        List<MockMultipartFile> files = getMockMultipartFileList(3);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(intro)
                .file(files.get(0))
                .file(files.get(1))
                .file(files.get(2))
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(3)));
    }

    @Test
    @DisplayName("???????????? ????????????")
    void save_lecture_curriculum() throws Exception {

        // given
        CreateLecture.CurriculumRequest dto = createCurriculumList();
        MockMultipartFile curriculum = getMockMultipartJson(dto);
        List<MockMultipartFile> files = getMockMultipartFileList(2);
        when(factory.getService(any())).thenReturn(putCurriculumService);
        when(putCurriculumService.putLectureProcess(any(), any()))
                .thenReturn(CreateLectureProcessResponse.completeCurriculum(1L));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(curriculum)
                .file(files.get(0))
                .file(files.get(1))
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestParts(
                                partWithName("request").description("???????????? ?????? ????????? ?????? JSON"),
                                partWithName("files").description("????????? ?????? ??????").attributes(field("constraints", "curriculumList size??? ?????? ?????? ?????? ?????? ??????"))
                        ),
                        requestPartFields("request",
                                fieldWithPath("createLectureStep").type(STRING).description(generateLinkCode(CREATE_LECTURE_STEP)),
                                fieldWithPath("curriculumList[].order").type(NUMBER).description("????????? ???????????? ??????"),
                                fieldWithPath("curriculumList[].title").type(STRING).description("????????? ???????????? ?????????").attributes(field("constraints", "5??? ?????? 50??? ??????")),
                                fieldWithPath("curriculumList[].detailText").type(STRING).description("???????????? ?????? ??????").attributes(field("constraints", "10??? ?????? 300??? ??????")),
                                fieldWithPath("curriculumList[].curriculumImageName").description("?????? ????????? ?????? ??????"),
                                fieldWithPath("curriculumList[].curriculumImageUrl").description("?????? ????????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("lectureId").description("???????????? ????????? ????????? ID"),
                                fieldWithPath("completedStepName").description(generateLinkCode(CREATE_LECTURE_STEP))
                        )
                ));
    }

    @Test
    @DisplayName("???????????? ???????????? / ???????????? ????????? ?????? ????????? ??????")
    void save_lecture_curriculum_should_not_input_curriculum() throws Exception {

        // given
        CreateLecture.CurriculumRequest dto = CreateLecture.CurriculumRequest.builder()
                .createLectureStep(CURRICULUM).build();
        MockMultipartFile curriculum = getMockMultipartJson(dto);
        List<MockMultipartFile> files = getMockMultipartFileList(2);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(curriculum)
                .file(files.get(0))
                .file(files.get(1))
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @Test
    @DisplayName("???????????? ???????????? / ???????????? ????????? ????????? ??????????????? ????????? ????????? ??????")
    void save_lecture_curriculum_should_not_constraint() throws Exception {

        // given
        List<MockMultipartFile> files = getMockMultipartFileList(2);
        CreateLecture.CurriculumRequest dto = createNotSatisfiedConstraintCurriculumList();
        MockMultipartFile curriculum = getMockMultipartJson(dto);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(curriculum)
                .file(files.get(0))
                .file(files.get(1))
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(8)));
    }

    @Test
    @DisplayName("????????? ????????????")
    void save_lecture_schedules() throws Exception {

        // given
        CreateLecture.ScheduleRequest dto = createScheduleRequest();
        MockMultipartFile json = getMockMultipartJson(dto);
        MockMultipartFile file = getMockMultipartFile();
        when(factory.getService(any())).thenReturn(putScheduleService);
        when(putScheduleService.putLectureProcess(any(), any()))
                .thenReturn(CreateLectureProcessResponse.completeSchedule(1L));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(json)
                .file(file)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestPartFields("request",
                                fieldWithPath("createLectureStep").type(STRING).description(generateLinkCode(CREATE_LECTURE_STEP)),
                                fieldWithPath("minParticipants").optional().type(NUMBER).description("????????? ?????? ?????? ??????").attributes(field("constraints","0 ?????? 1??? ??????")),
                                fieldWithPath("maxParticipants").type(NUMBER).description("????????? ?????? ?????? ??????").attributes(field("constraints","0 ?????? 1??? ??????")),
                                fieldWithPath("scheduleList[]").type(ARRAY).description("????????? ?????????"),
                                fieldWithPath("scheduleList[].progressMinute").type(NUMBER).description("?????? ?????? (??? ??????)").attributes(field("constraints","?????? 60 ??????, 30?????? ???????????? ??????.")),
                                fieldWithPath("scheduleList[].lectureDate").type(STRING).description("?????? ??????").attributes(field("constraint", "yyyy-MM-dd ???????????? ??????")),
                                fieldWithPath("scheduleList[].startHour").description("?????? ??????").attributes(field("constraint", "0 ~ 23")),
                                fieldWithPath("scheduleList[].startMinute").description("?????? ???").attributes(field("constraint", "0 ~ 59 && 30?????? ???????????? ??????.")),
                                fieldWithPath("scheduleList[].endHour").optional().description("?????? ?????? ???").attributes(field("constraint", "NULL")),
                                fieldWithPath("scheduleList[].endMinute").optional().description("?????? ?????? ???").attributes(field("constraint", "NULL"))
                        ),
                        responseFields(
                                fieldWithPath("lectureId").description("???????????? ????????? ????????? ID"),
                                fieldWithPath("completedStepName").description(generateLinkCode(CREATE_LECTURE_STEP))
                        )
                ));
    }

    @Test
    @DisplayName("????????? ???????????? / Date ??? ???, startHour, endHour ???????????? ????????????")
    void save_lecture_schedules_should_follow_date_and_hour() throws Exception {

        // given
        CreateLecture.ScheduleRequest dto = createScheduleNonConstraintRequest();
        MockMultipartFile json = getMockMultipartJson(dto);
        MockMultipartFile file = getMockMultipartFile();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(json)
                .file(file)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(5)));
    }

    @Test
    @DisplayName("????????? ???????????? / ????????? ?????? ?????? ???, startMinute 60 ??????, participants ?????? ???????????? ????????????")
    void save_lecture_schedules_should_divide_and_constraint_minute() throws Exception {

        // given
        CreateLecture.ScheduleRequest dto = createScheduleNonConstraintRequest();
        dto.getScheduleList().get(0).setStartMinute(90);
        dto.setMinParticipants(-1);
        dto.setMaxParticipants(-1);
        MockMultipartFile json = getMockMultipartJson(dto);
        MockMultipartFile file = getMockMultipartFile();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(json)
                .file(file)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(7)));
    }

    @Test
    @DisplayName("?????? ??? ?????? ?????? ????????????")
    void save_lecture_price_coupon() throws Exception {

        // given
        CreateLecture.PriceCouponRequest dto = createPriceCouponRequest();
        MockMultipartFile json = getMockMultipartJson(dto);
        MockMultipartFile file = getMockMultipartFile();
        when(factory.getService(any())).thenReturn(putPriceCouponService);
        when(putPriceCouponService.putLectureProcess(any(), any()))
                .thenReturn(CreateLectureProcessResponse.completePriceCoupon(1L));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload(URL)
                .file(json)
                .file(file)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .with(req -> {
                    req.setMethod(PUT);
                    return req;
                }).contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestPartFields("request",
                                fieldWithPath("createLectureStep").type(STRING).description(generateLinkCode(CREATE_LECTURE_STEP)),
                                fieldWithPath("price.regularPrice").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("price.priceOne").type(NUMBER).description("????????? 1??? ?????? ??????"),
                                fieldWithPath("price.priceTwo").type(NUMBER).description("????????? 2??? ?????? ??????"),
                                fieldWithPath("price.priceThree").type(NUMBER).description("????????? 3??? ?????? ??????"),
                                fieldWithPath("price.priceFour").type(NUMBER).description("????????? 4??? ?????? ??????"),
                                fieldWithPath("coupon.couponPrice").type(NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("coupon.couponCount").type(NUMBER).description("?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("lectureId").description("???????????? ????????? ????????? ID"),
                                fieldWithPath("completedStepName").description(generateLinkCode(CREATE_LECTURE_STEP))
                        )
                ));
    }

    @Test
    @DisplayName("?????? ???????????? ??? ????????? ?????? ??????")
    void save_lecture_terms() throws Exception {

        // given
        CreateLecture.TermsRequest dto = CreateLecture.TermsRequest.builder().createLectureStep(TERMS)
                .termsOne(true).termsTwo(true).termsThree(true).termsFour(true).build();
        when(temporaryStorageLectureManageService.saveLecture(any()))
                .thenReturn(TemporaryStorageLectureManageResponse.save(1L));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJsonBody(dto))
                .contentType(APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        requestFields(
                                fieldWithPath("createLectureStep").description(generateLinkCode(CREATE_LECTURE_STEP)),
                                fieldWithPath("termsOne").type(BOOLEAN).description("?????? ?????? ?????? 1").attributes(field("constraints", "????????? true")),
                                fieldWithPath("termsTwo").type(BOOLEAN).description("?????? ?????? ?????? 2").attributes(field("constraints", "????????? true")),
                                fieldWithPath("termsThree").type(BOOLEAN).description("?????? ?????? ?????? 3").attributes(field("constraints", "????????? true")),
                                fieldWithPath("termsFour").type(BOOLEAN).description("?????? ?????? ?????? 4").attributes(field("constraints", "????????? true"))
                        ),
                        responseFields(
                                fieldWithPath("lectureId").description("?????? ????????? ????????? ????????? ????????? ID"),
                                fieldWithPath("resultResponse").description(generateLinkCode(LECTURE_SAVE_DELETE_RESPONSE)),
                                fieldWithPath("description").description("????????? ?????? ?????? ??????")
                        )
                ));
        verify(temporaryStorageLectureManageService).saveLecture(any());
    }

    @Test
    @DisplayName("????????? ???????????? ?????? ?????? ?????? ??????")
    void save_lecture_terms_should_all_true() throws Exception {

        // given
        CreateLecture.TermsRequest dto = CreateLecture.TermsRequest.builder().createLectureStep(TERMS)
                .termsOne(false).termsTwo(false).termsThree(false).termsFour(false).build();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJsonBody(dto))
                .contentType(APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(4)));
    }

    @Test
    @DisplayName("????????? ????????? ????????? ?????? ????????? ????????? ?????? ???????????? / ???????????? ??????")
    void get_temporary_stored_lecture_not_found() throws Exception {

        // given
        doThrow(new LectureException(LectureErrorCodes.TEMPORARY_NOT_SAVE_LECTURE))
                .when(temporaryStorageLectureManageService).getTemporaryStoredLecture();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("LECTURE-404")));
    }

    @Test
    @DisplayName("?????? ????????? ????????? ????????????")
    void delete_temporary_store_lecture() throws Exception {

        // given
        when(temporaryStorageLectureManageService.deleteTemporaryStorageLecture())
                .thenReturn(TemporaryStorageLectureManageResponse.delete());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .contentType(APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultResponse", is("DELETE")))
                .andExpect(jsonPath("$.description", is("?????? ????????? ????????? ????????? ?????????????????????.")))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("lectureId").description("?????? ????????? ????????? ????????? ????????? ID"),
                                fieldWithPath("resultResponse").description(generateLinkCode(LECTURE_SAVE_DELETE_RESPONSE)),
                                fieldWithPath("description").description("????????? ?????? ?????? ??????")
                        )
                ));
    }

    private CreateLecture.PriceCouponRequest createPriceCouponRequest() {
        return CreateLecture.PriceCouponRequest.builder().createLectureStep(PRICE_COUPON)
                .price(CreateLecture.PriceDto.builder().regularPrice(30000).priceOne(30000).priceTwo(29000).priceThree(28000).priceFour(27000).build())
                .coupon(CreateLecture.CouponDto.builder().couponCount(50).couponPrice(2000).build())
                .build();
    }

    private CreateLecture.ScheduleRequest createScheduleNonConstraintRequest() {
        return CreateLecture.ScheduleRequest.builder().createLectureStep(SCHEDULE)
                .minParticipants(1).maxParticipants(6)
                .scheduleList(List.of(
                        CreateLecture.ScheduleDto.builder()
                                .progressMinute(59)
                                .lectureDate(null)
                                .startHour(24)
                                .startMinute(60)
                                .build()
                        )
                )
                .build();
    }

    private CreateLecture.ScheduleRequest createScheduleRequest() {
        return CreateLecture.ScheduleRequest.builder().createLectureStep(SCHEDULE)
                .minParticipants(1).maxParticipants(6)
                .scheduleList(List.of(
                        CreateLecture.ScheduleDto.builder()
                                .progressMinute(60)
                                .lectureDate(LocalDate.of(2021, 3, 2))
                                .startHour(17)
                                .startMinute(30)
                                .build(),
                        CreateLecture.ScheduleDto.builder()
                                .progressMinute(60)
                                .lectureDate(LocalDate.of(2021, 3, 3))
                                .startHour(17)
                                .startMinute(30)
                                .build()))
                .build();
    }

    private CreateLecture.CurriculumRequest createNotSatisfiedConstraintCurriculumList() {
        return CreateLecture.CurriculumRequest.builder().createLectureStep(CURRICULUM)
                .curriculumList(List.of(
                        CreateLecture.CurriculumDto.builder().title(" ").detailText(" ").build(),
                        CreateLecture.CurriculumDto.builder().title(" ").detailText(" ").build()
                ))
                .build();
    }

    private CreateLecture.IntroRequest createEmptyOrderTextListAndNotOver10MainText() {
        return CreateLecture.IntroRequest.builder().createLectureStep(INTRO)
                .mainText("test")
                .finishedProductInfoList(List.of(
                        CreateLecture.FinishedProductInfoDto.builder().build(),
                        CreateLecture.FinishedProductInfoDto.builder().build()))
                .build();
    }

    private CreateLecture.CurriculumRequest createCurriculumList() {
        return CreateLecture.CurriculumRequest.builder().createLectureStep(CURRICULUM)
                .curriculumList(List.of(
                        CreateLecture.CurriculumDto.builder().order(0).detailText("testtesttest").title("titletitle").build(),
                        CreateLecture.CurriculumDto.builder().order(1).detailText("testtesttest").title("titletitle").build()
                ))
                .build();
    }

    private CreateLecture.IntroRequest createIntro() {
        return CreateLecture.IntroRequest.builder().createLectureStep(INTRO)
                .mainText("testtesttesttest")
                .finishedProductInfoList(List.of(
                        CreateLecture.FinishedProductInfoDto.builder()
                                .order(0).text("testtesttesttest")
                                .build(),
                        CreateLecture.FinishedProductInfoDto.builder()
                                .order(1).text("testtesttesttest")
                                .build(),
                        CreateLecture.FinishedProductInfoDto.builder()
                                .order(2).text("testtesttesttest")
                                .build()))
                .build();
    }

    private MockMultipartFile getMockMultipartJson(Object dto) throws JsonProcessingException {
        return new MockMultipartFile("request",
                "first",
                MediaType.APPLICATION_JSON_VALUE,
                createJsonBody(dto).getBytes());
    }

    private List<MockMultipartFile> getMockMultipartFileList(int count) {
        List<MockMultipartFile> files = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            files.add(getMockMultipartFile());
        }
        return files;
    }

    private MockMultipartFile getMockMultipartFile() {
        return new MockMultipartFile("files",
                "image.img",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "<<image>>".getBytes());
    }

    private String createJsonBody(Object request) throws JsonProcessingException {
        return objectMapper.writeValueAsString(request);
    }
}