package com.gjgs.gjgs.modules.payment.controller;

import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.lecture.exceptions.ScheduleErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.ScheduleException;
import com.gjgs.gjgs.modules.payment.dto.OrderIdDto;
import com.gjgs.gjgs.modules.payment.dto.PaymentRequest;
import com.gjgs.gjgs.modules.payment.dto.PaymentVerifyRequest;
import com.gjgs.gjgs.modules.payment.dto.TeamMemberPaymentResponse;
import com.gjgs.gjgs.modules.payment.exceptions.OrderErrorCodes;
import com.gjgs.gjgs.modules.payment.exceptions.OrderException;
import com.gjgs.gjgs.modules.payment.exceptions.PaymentErrorCodes;
import com.gjgs.gjgs.modules.payment.exceptions.PaymentException;
import com.gjgs.gjgs.modules.payment.service.order.OrderService;
import com.gjgs.gjgs.modules.payment.service.pay.PaymentPersonalProcessImpl;
import com.gjgs.gjgs.modules.payment.service.pay.PaymentServiceFactory;
import com.gjgs.gjgs.modules.payment.service.pay.PaymentTeamMemberProcessImpl;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.IOException;

import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.DocUrl.PAY_TYPE;
import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.generateLinkCode;
import static com.gjgs.gjgs.document.utils.RestDocsConfig.field;
import static java.time.LocalDateTime.now;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(
        value = { PaymentController.class }
)
class PaymentControllerTest extends RestDocsTestSupport {

    private final String SCHEDULE_URL = "/api/v1/payment/{payType}/{scheduleId}";
    private final String ORDER_URL = "/api/v1/payment/{payType}/{orderId}";

    @MockBean PaymentServiceFactory paymentServiceFactory;
    @MockBean OrderService orderService;
    @MockBean PaymentPersonalProcessImpl paymentPersonalProcess;
    @MockBean PaymentTeamMemberProcessImpl paymentTeamMemberProcess;

    @BeforeEach
    void setUpMockUser() {
        securityUserMockSetting();
    }

    @Test
    @DisplayName("?????? ?????? 1. ?????? ????????? ?????? ??????")
    void common_exception_not_found_pay_type() throws Exception {

        // given
        when(paymentServiceFactory.getProcess(any()))
                .thenThrow(new PaymentException(PaymentErrorCodes.INVALID_PAY_TYPE));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(SCHEDULE_URL, "TEAM", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(PaymentRequest.builder().lectureId(1L).build()))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("?????? ?????? 2. ?????? ????????? ????????? ?????? ????????? ??????")
    void common_exception_not_valid_close_time() throws Exception {

        // given
        when(paymentServiceFactory.getProcess(any()))
                .thenReturn(paymentTeamMemberProcess);
        when(paymentTeamMemberProcess.payProcess(any(), any()))
                .thenThrow(new ScheduleException(ScheduleErrorCodes.SCHEDULE_OVER_TIME));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(SCHEDULE_URL, "TEAM", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(PaymentRequest.builder().lectureId(1L).build()))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("?????? ?????? 3. ???????????? ?????? ?????? ??????")
    void common_exception_iamport() throws Exception {

        // given
        when(orderService.verifyAndCompletePayment(any()))
                .thenThrow(new IamportResponseException("???????????? ?????? ??????, ?????? ????????? ????????? ??? ????????????.",
                        new HttpException(Response.error(400, ResponseBody.create(null, "")))));

        // when, then

        mockMvc.perform(RestDocumentationRequestBuilders.patch(SCHEDULE_URL, "TEAM", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(PaymentVerifyRequest.builder().orderId(1L).iamportUid("test").build()))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("?????? ?????? 4. ????????? ????????? ?????? ?????? ??????")
    void common_exception_invalid_payment() throws Exception {

        // given
        String json = createJson(createPaymentRequest());
        stubbingPersonalPayment();
        when(paymentPersonalProcess.payProcess(any(), any()))
                .thenThrow(new PaymentException(PaymentErrorCodes.INVALID_PRICE));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(SCHEDULE_URL, "PERSONAL", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(json)
                .contentType(APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("??? ????????? ?????? ?????? ????????????")
    void get_team_members_payment() throws Exception {

        // given
        when(orderService.getTeamMemberPayment(any()))
                .thenReturn(createTeamMemberPaymentResponse());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(SCHEDULE_URL, "TEAM", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("payType").description(generateLinkCode(PAY_TYPE)),
                                parameterWithName("scheduleId").description("????????? ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("orderId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("price").type(NUMBER).description("?????? ??????"),
                                fieldWithPath("scheduleId").type(NUMBER).description("????????? ????????? ID"),
                                fieldWithPath("startTime").type(STRING).description("?????? ??????"),
                                fieldWithPath("endTime").type(STRING).description("?????? ??????"),
                                fieldWithPath("lectureThumbnailUrl").type(STRING).description("????????? ????????? URL"),
                                fieldWithPath("lectureTitle").type(STRING).description("????????? ??????"),
                                fieldWithPath("teamId").type(NUMBER).description("??? ID"),
                                fieldWithPath("teamName").type(STRING).description("??? ??????"),
                                fieldWithPath("haveReward").type(NUMBER).description("?????? ???????????? ?????????")
                        )
                ));
    }

    private TeamMemberPaymentResponse createTeamMemberPaymentResponse() {
        return TeamMemberPaymentResponse.builder()
                .orderId(1L)
                .price(3000)
                .scheduleId(1L)
                .startTime(now())
                .endTime(now().plusHours(2L))
                .lectureThumbnailUrl("test")
                .lectureTitle("test")
                .teamId(1L)
                .teamName("test")
                .haveReward(1000)
                .build();
    }

    @Test
    @DisplayName("???????????? ????????? ????????? ????????????")
    void pay_team_member() throws Exception {

        // given
        String json = createJson(createPaymentRequest());
        stubbingPersonalPayment();
        when(paymentPersonalProcess.payProcess(any(), any()))
                .thenReturn(OrderIdDto.ofComplete(1L));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(SCHEDULE_URL, "PERSONAL", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(json)
                .contentType(APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("payType").description(generateLinkCode(PAY_TYPE)),
                                parameterWithName("scheduleId").description("????????? ????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("lectureId").type(NUMBER).description("????????? ????????? ID"),
                                fieldWithPath("memberCouponId").type(NUMBER).description("????????? ?????? ID"),
                                fieldWithPath("rewardAmount").type(NUMBER).description("????????? ????????? ??? ??????"),
                                fieldWithPath("totalPrice").type(NUMBER).description("????????? ????????? ??????").attributes(field("constraints", "????????? ?????? ?????? ??? ?????? ??????"))
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("?????? ????????? ?????? ID"),
                                fieldWithPath("description").description("????????? ?????? ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("???????????? ????????? ????????? ???????????? / ????????? ????????? ID??? ???????????? ?????? ?????? ?????? ??????")
    void pay_team_member_should_input_lecture_id() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(SCHEDULE_URL, "PERSONAL", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(PaymentRequest.builder().build()))
                .contentType(APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @Test
    @DisplayName("?????? ????????????")
    void verify_payment() throws Exception {

        // given
        String json = createJson(createPaymentVerifyRequest());
        stubbingVerifyPayment();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(SCHEDULE_URL, "PERSONAL", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(json)
                .contentType(APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("payType").description(generateLinkCode(PAY_TYPE)),
                                parameterWithName("scheduleId").description("????????? ????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("orderId").type(NUMBER).description("????????? ?????? ID"),
                                fieldWithPath("iamportUid").type(STRING).description("?????????????????? ?????? ?????? ??? ?????? UID")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("?????? ????????? ?????? ID"),
                                fieldWithPath("description").description("????????? ?????? ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ???????????? / ????????? ?????? ?????? ?????? ??????????????? ???")
    void verify_payment_auto_cancel() throws Exception {

        // given
        String json = createJson(createPaymentVerifyRequest());
        stubbingVerifyPaymentAutoCancel();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(SCHEDULE_URL, "PERSONAL", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(json)
                .contentType(APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("payType").description(generateLinkCode(PAY_TYPE)),
                                parameterWithName("scheduleId").description("????????? ????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("orderId").type(NUMBER).description("????????? ?????? ID"),
                                fieldWithPath("iamportUid").type(STRING).description("?????????????????? ?????? ?????? ??? ?????? UID")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("?????? ????????? ?????? ID"),
                                fieldWithPath("description").description("????????? ?????? ?????? ??????")
                        )
                ));
    }

    private void stubbingVerifyPaymentAutoCancel() throws IamportResponseException, IOException {
        when(orderService.verifyAndCompletePayment(any()))
                .thenReturn(OrderIdDto.ofCancel(1L));
    }

    @Test
    @DisplayName("?????? ???????????? / ?????? ID??? ???????????? UID??? ?????? ?????? ?????? ??????")
    void verify_payment_should_input_order_id_and_iamport_uid() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(SCHEDULE_URL, "PERSONAL", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .content(createJson(PaymentVerifyRequest.builder().build()))
                .contentType(APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }

    private void stubbingVerifyPayment() throws IamportResponseException, IOException {
        when(orderService.verifyAndCompletePayment(any())).thenReturn(OrderIdDto.ofComplete(1L));
    }

    @Test
    @DisplayName("?????? ????????????")
    void cancel_payment() throws Exception {

        // given
        stubbingTeamPayment();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(ORDER_URL, "TEAM", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("orderId").description("?????? ID"),
                                parameterWithName("payType").description(generateLinkCode(PAY_TYPE))
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? / ????????? ?????? 3??? ????????? ????????? ?????? ??????")
    void cancel_payment_should_not_cancel_under_three_days() throws Exception {

        // given
        stubbingTeamPayment();
        stubbingCancelExceptionPayment();

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(ORDER_URL, "TEAM", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    private void stubbingCancelExceptionPayment() throws IamportResponseException, IOException {
        doThrow(new OrderException(OrderErrorCodes.ORDER_NOT_CANCEL))
                .when(paymentTeamMemberProcess).cancelProcess(any());
    }


    private OrderIdDto createOrderIdDto() {
        return OrderIdDto.ofComplete(1L);
    }

    private PaymentVerifyRequest createPaymentVerifyRequest() {
        return PaymentVerifyRequest.builder()
                .iamportUid("imp_12341234")
                .orderId(1L)
                .build();
    }

    private void stubbingPersonalPayment() {
        when(paymentServiceFactory.getProcess(any())).thenReturn(paymentPersonalProcess);
    }

    private void stubbingTeamPayment() {
        when(paymentServiceFactory.getProcess(any())).thenReturn(paymentTeamMemberProcess);
    }

    private PaymentRequest createPaymentRequest() {
        return PaymentRequest.builder()
                .lectureId(1L)
                .memberCouponId(1L)
                .rewardAmount(1000)
                .totalPrice(2000)
                .build();
    }
}
