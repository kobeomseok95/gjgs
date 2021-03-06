package com.gjgs.gjgs.modules.coupon.controllers;

import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.coupon.dto.EnableMemberCouponResponse;
import com.gjgs.gjgs.modules.coupon.exception.CouponErrorCodes;
import com.gjgs.gjgs.modules.coupon.exception.CouponException;
import com.gjgs.gjgs.modules.coupon.services.MemberCouponServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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


@WebMvcTest(
        value = { MemberCouponController.class}
)
class MemberCouponControllerTest extends RestDocsTestSupport {

    private final String COUPON_API = "/api/v1/lectures/{lectureId}/coupon";

    @MockBean MemberCouponServiceImpl memberCouponService;

    @BeforeEach
    void setUpMockUser() {
        securityUserMockSetting();
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    void get_member_coupon() throws Exception {

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(COUPON_API, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("lectureId").description("????????? ?????? ?????? ????????? ID")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ???????????? / ?????? ?????? ????????? ???????????? ???")
    void get_member_coupon_should_not_coupon_exception() throws Exception {

        // given
        doThrow(new CouponException(CouponErrorCodes.INVALID_COUPON)).when(memberCouponService).giveMemberCoupon(any());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post(COUPON_API, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("?????? ??? ?????? ????????? ??? ?????? ?????? ????????????")
    void get_enable_member_coupon() throws Exception {

        // given
        when(memberCouponService.getMemberCoupon(any())).thenReturn(EnableMemberCouponResponse.builder()
                .enableCouponList(Set.of(
                        EnableMemberCouponResponse.EnableCoupon.builder()
                                .memberCouponId(1L).couponTitle("???????????????").discountPrice(1000)
                                .build()
                ))
                .build());

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get(COUPON_API, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enableCouponList[0].memberCouponId", is(1)))
                .andExpect(jsonPath("$.enableCouponList[0].couponTitle", is("???????????????")))
                .andExpect(jsonPath("$.enableCouponList[0].discountPrice", is(1000)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        ),
                        pathParameters(
                                parameterWithName("lectureId").description("????????? ??? ?????? ????????? ????????? ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("enableCouponList[0].memberCouponId").type(NUMBER).description("????????? ???????????? ?????? ?????????"),
                                fieldWithPath("enableCouponList[0].couponTitle").type(STRING).description("?????? ??????"),
                                fieldWithPath("enableCouponList[0].discountPrice").type(NUMBER).description("?????? ??????")
                        )
                ));
    }
}
