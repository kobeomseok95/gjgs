package com.gjgs.gjgs.modules.lecture.controllers;

import com.gjgs.gjgs.document.utils.DocumentLinkGenerator;
import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.lecture.dtos.admin.ConfirmLectureResponse;
import com.gjgs.gjgs.modules.lecture.dtos.admin.RejectReason;
import com.gjgs.gjgs.modules.lecture.services.admin.AdminLectureServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.List;

import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.generateLinkCode;
import static com.gjgs.gjgs.modules.lecture.dtos.admin.DecideLectureType.ACCEPT;
import static com.gjgs.gjgs.modules.lecture.dtos.admin.DecideLectureType.REJECT;
import static java.time.LocalDateTime.now;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(
        value = {AdminLectureController.class}
)
class AdminLectureControllerTest extends RestDocsTestSupport {

    private final String ADMIN_URL = "/api/v1/admin/lectures";

    @MockBean AdminLectureServiceImpl adminLectureService;

    @BeforeEach
    void setUpMockUser() {
        securityAdminMockSetting();
    }

    @Test
    @DisplayName("????????? ???????????? ????????????")
    void get_confirm_lectures() throws Exception {

        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(adminLectureService.getConfirmLectures(any()))
                .thenReturn(new PageImpl<>(createConfirmLectureResponse(), pageRequest, 1));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(ADMIN_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        responseFields(
                                fieldWithPath("content[].lectureId").type(NUMBER).description("??????????????? ??? ????????? ID"),
                                fieldWithPath("content[].lectureTitle").type(STRING).description("????????? ???"),
                                fieldWithPath("content[].confirmDateTime").type(STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("content[].directorNickname").type(STRING).description("????????? ?????????"),
                                fieldWithPath("content[].categoryId").type(NUMBER).description("?????? ???????????? ID"),
                                fieldWithPath("content[].categoryName").type(STRING).description("?????? ???????????? ???"),
                                fieldWithPath("content[].zoneId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("content[].zoneName").type(STRING).description("?????? ???")
                        ).and(pageDescriptor())
                ));
    }

    private List<ConfirmLectureResponse> createConfirmLectureResponse() {
        return List.of(
                ConfirmLectureResponse.builder()
                        .lectureId(1L)
                        .lectureTitle("test")
                        .confirmDateTime(now())
                        .directorNickname("test")
                        .categoryId(1L)
                        .categoryName("test")
                        .zoneId(1L)
                        .zoneName("test")
                        .build()
        );
    }

    @Test
    @DisplayName("????????? ????????? ?????? ?????? ????????????")
    void get_confirm_lecture() throws Exception {

        // given
        when(adminLectureService.getConfirmLecture(any()))
                .thenReturn(PutLectureResponseDtoStub.getPutLectureResponseDto("CONFIRM"));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(ADMIN_URL + "/{lectureId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(parameterWithName("lectureId").description("?????? ????????? ??? ????????? ID")),
                        responseFields(
                                fieldWithPath("lectureId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("categoryId").type(NUMBER).description("?????? ???????????? ID"),
                                fieldWithPath("zoneId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("title").type(STRING).description("????????? ??????"),
                                fieldWithPath("address").type(STRING).description("????????? ??????"),
                                fieldWithPath("thumbnailImageFileName").type(STRING).description("?????? ?????????"),
                                fieldWithPath("thumbnailImageFileUrl").type(STRING).description("?????? ?????? URL"),
                                fieldWithPath("minParticipants").type(NUMBER).description("???????????? ?????? ?????? ??????"),
                                fieldWithPath("maxParticipants").type(NUMBER).description("???????????? ?????? ?????? ??????"),
                                fieldWithPath("mainText").type(STRING).description("?????????"),
                                fieldWithPath("lectureStatus").type(STRING).description("????????? ?????? ??????"),
                                fieldWithPath("finishedProductList[].finishedProductId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("finishedProductList[].orders").type(NUMBER).description("???????????? ??????"),
                                fieldWithPath("finishedProductList[].text").type(STRING).description("????????? ??????"),
                                fieldWithPath("finishedProductList[].finishedProductImageName").type(STRING).description("????????? ?????????"),
                                fieldWithPath("finishedProductList[].finishedProductImageUrl").type(STRING).description("????????? ?????? URL"),
                                fieldWithPath("curriculumList[].curriculumId").type(NUMBER).description("???????????? ID"),
                                fieldWithPath("curriculumList[].orders").type(NUMBER).description("??????????????? ??????"),
                                fieldWithPath("curriculumList[].title").type(STRING).description("???????????? ???"),
                                fieldWithPath("curriculumList[].detailText").type(STRING).description("???????????? ?????? ??????"),
                                fieldWithPath("curriculumList[].curriculumImageName").type(STRING).description("???????????? ?????????"),
                                fieldWithPath("curriculumList[].curriculumImageUrl").type(STRING).description("???????????? ?????? URL"),
                                fieldWithPath("scheduleList[].scheduleId").type(NUMBER).description("????????? ID"),
                                fieldWithPath("scheduleList[].lectureDate").type(STRING).description("????????? ?????? ??????"),
                                fieldWithPath("scheduleList[].startHour").type(NUMBER).description("????????? ?????? ?????? ??????"),
                                fieldWithPath("scheduleList[].startMinute").type(NUMBER).description("????????? ?????? ?????? ???"),
                                fieldWithPath("scheduleList[].endHour").type(NUMBER).description("????????? ??? ??????"),
                                fieldWithPath("scheduleList[].endMinute").type(NUMBER).description("????????? ??? ???"),
                                fieldWithPath("scheduleList[].progressMinute").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("price.regularPrice").type(NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("price.priceOne").type(NUMBER).description("????????? 1??? ?????? ??????"),
                                fieldWithPath("price.priceTwo").type(NUMBER).description("????????? 2??? ?????? ??????"),
                                fieldWithPath("price.priceThree").type(NUMBER).description("????????? 3??? ?????? ??????"),
                                fieldWithPath("price.priceFour").type(NUMBER).description("????????? 4??? ?????? ??????"),
                                fieldWithPath("coupon.couponPrice").type(NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("coupon.couponCount").type(NUMBER).description("????????? ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ????????????")
    void accept_lecture() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(ADMIN_URL + "/{lectureId}/{decideType}", 1, ACCEPT.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("lectureId").description("????????? ????????? ????????? ID"),
                                parameterWithName("decideType").description(generateLinkCode(DocumentLinkGenerator.DocUrl.LECTURE_CONFIRM_DECIDE_TYPE))
                        )
                ));
    }

    @Test
    @DisplayName("????????? ???????????? / ?????? ????????? ?????? ?????? ?????? ?????? ??????")
    void reject_lecture_should_write_reject_reason() throws Exception {

        // given
        RejectReason reason = RejectReason.builder().build();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(ADMIN_URL + "/{lectureId}/{decideType}", 1, REJECT.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(reason))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @Test
    @DisplayName("????????? ???????????? / ?????? ????????? 10??? ????????? ?????? ?????? ??????")
    void reject_lecture_should_min_10_length_reject_reason() throws Exception {

        // given
        RejectReason reason = RejectReason.builder().rejectReason("123").build();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(ADMIN_URL + "/{lectureId}/{decideType}", 1, REJECT.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(reason))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }
}
