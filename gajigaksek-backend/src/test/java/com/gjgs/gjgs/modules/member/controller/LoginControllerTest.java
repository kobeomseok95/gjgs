package com.gjgs.gjgs.modules.member.controller;

import com.gjgs.gjgs.document.utils.RestDocsTestSupport;
import com.gjgs.gjgs.modules.category.repositories.CategoryRepository;
import com.gjgs.gjgs.modules.dummy.MemberDummy;
import com.gjgs.gjgs.modules.member.dto.login.*;
import com.gjgs.gjgs.modules.member.service.login.interfaces.LoginService;
import com.gjgs.gjgs.modules.member.validator.SignUpRequestValidator;
import com.gjgs.gjgs.modules.zone.repositories.interfaces.ZoneRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.DocUrl;
import static com.gjgs.gjgs.document.utils.DocumentLinkGenerator.generateLinkCode;
import static com.gjgs.gjgs.document.utils.RestDocsConfig.field;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = {LoginController.class, SignUpRequestValidator.class}
)
class LoginControllerTest extends RestDocsTestSupport {

    final String LOGIN = "/api/v1/login";
    final String LOGOUT = "/api/v1/logout";
    final String SIGNUP = "/api/v1/sign-up";
    final String KAKAO_TOKEN_HEADER = "KakaoAccessToken";
    final String BEARER_ACCESS_TOKEN = "Bearer access_token";
    final String BEARER_KAKAO_TOKEN = "Bearer kakao_token";
    final String BEARER_REFRESH_TOKEN = "Bearer refresh_token";
    final String REFRESH_TOKEN_HEADER = "RefreshToken";

    @MockBean LoginService loginService;
    @MockBean CategoryRepository categoryRepository;
    @MockBean ZoneRepository zoneRepository;

    @DisplayName("?????? ????????? Bearer??? ?????? ???")
    @Test
    void common_errors_should_token_grant_type_is_bearer() throws Exception {

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "test "))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value("KAKAO-401"));

    }

    @DisplayName("??????????????? ?????? ??????")
    @Test
    void common_errors_should_not_request_not_normal_token() throws Exception {
        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + "invalid"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value("KAKAO-401"));
    }

    @DisplayName("????????? ??????")
    @Test
    void login() throws Exception {
        //given
        String fcmToken = "testFcmToken";
        FcmTokenRequest fcmTokenRequest = FcmTokenRequest.builder()
                .fcmToken(fcmToken)
                .build();
        LoginResponse loginResponse = LoginResponse.createTokenLoginResponse(
                1L,
                createTestTokenDto(),
                KakaoProfile.builder()
                        .id(1L)
                        .properties(KakaoProfile.Properties.builder()
                                .profile_image("profile_image_url")
                                .build())
                        .build()
        );
        when(loginService.login(any(),any())).thenReturn(loginResponse);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(LOGIN + "/{provider}", "kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fcmTokenRequest))
                .header(KAKAO_TOKEN_HEADER, BEARER_KAKAO_TOKEN))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("provider").description("?????? ????????? ?????????")
                        ),
                        requestHeaders(
                                headerWithName("kakaoAccessToken").description("????????? access token")
                        ),
                        requestFields(
                                fieldWithPath("fcmToken").type(STRING).description("fcm token")
                        ),
                        responseFields(
                                fieldWithPath("tokenDto.grantType").type(STRING).description("?????? ??????"),
                                fieldWithPath("tokenDto.accessToken").type(STRING).description("JWT Access Token"),
                                fieldWithPath("tokenDto.refreshToken").type(STRING).description("JWT Refresh Token"),
                                fieldWithPath("tokenDto.accessTokenExpiresIn").type(NUMBER).description("JWT Access Token ?????? ??????"),
                                fieldWithPath("tokenDto.refreshTokenExpiresIn").type(NUMBER).description("JWT Refresh Token ?????? ??????"),
                                fieldWithPath("memberId").type(NUMBER).description("?????? ID"),
                                fieldWithPath("id").type(NUMBER).description("????????? ?????? ?????? ID"),
                                fieldWithPath("imageFileUrl").type(STRING).description("????????? ????????? URL")
                        )
                ))
        ;
    }

    @DisplayName("FcmTokenForm @valid ??????????????? ??????")
    @Test
    void login_should_valid_fcm_token_form() throws Exception {
        // given
        SignUpRequest signUpRequest = MemberDummy.createInvalidSignupForm();
        String fcmToken = "";

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(LOGIN + "/{provider}", "kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fcmToken))
                .header(KAKAO_TOKEN_HEADER, BEARER_KAKAO_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @DisplayName("????????? ?????? ???, ?????? ????????? ?????? ??????")
    @Test
    void login_fail_redirect_sign_up() throws Exception {
        //given
        FcmTokenRequest fcmTokenRequest = FcmTokenRequest.builder()
                .fcmToken("FcmToken")
                .build();

        LoginResponse loginResponse = LoginResponse.createKakaoProfileLoginResponse(
                KakaoProfile.builder()
                    .id(1L)
                    .properties(KakaoProfile.Properties.builder()
                            .profile_image("profile image url")
                            .build()
                    )
                .build());
        when(loginService.login(any(),any())).thenReturn(loginResponse);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(LOGIN + "/{provider}", "kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson(fcmTokenRequest))
                .header(KAKAO_TOKEN_HEADER, BEARER_KAKAO_TOKEN))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("fcmToken").description("FCM ??????")
                        ),
                        responseFields(
                                fieldWithPath("tokenDto").description("????????? ?????? ???"),
                                fieldWithPath("memberId").description("?????? Id ???"),
                                fieldWithPath("id").description("??????????????? ???????????? id???"),
                                fieldWithPath("imageFileUrl").description("??????????????? ???????????? ????????? ????????? url")
                        )
                ))

        ;
    }

    @DisplayName("????????? ????????? fcmTokenRequest ??? ???????????? ??????")
    @Test
    void login_fcmTokenRequest_valid_check() throws Exception {
        //given
        FcmTokenRequest fcmTokenRequest = FcmTokenRequest.builder().fcmToken("").build();

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(LOGIN + "/{provider}", "kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson(fcmTokenRequest))
                .header(KAKAO_TOKEN_HEADER, BEARER_KAKAO_TOKEN))
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document(
                        responseFields(errorDescriptor())
                ))
        ;
    }


    @DisplayName("????????? ?????? ???, ?????? ????????? ???????????? ?????????????????? sign up ?????? ?????? - ???????????? + ?????????")
    @Test
    void sign_up() throws Exception {
        // given
        SignUpRequest signUpRequest = MemberDummy.createSignupForm();

        TokenDto tokenDto = createTestTokenDto();
        LoginResponse loginResponse = LoginResponse.createTokenLoginResponse(
                1L,
                tokenDto,
                signUpRequest.getId(),
                signUpRequest.getImageFileUrl()
                );

        when(memberRepository.existsByUsername(any())).thenReturn(false);
        when(zoneRepository.existsById(any())).thenReturn(true);
        when(categoryRepository.countCategoryByIdList(any())).thenReturn(3L);
        when(memberRepository.existsByNickname(any()))
                .thenReturn(false)
                .thenReturn(true);
        when(loginService.saveAndLogin(any())).thenReturn(loginResponse);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("tokenDto.grantType").value(tokenDto.getGrantType()))
                .andExpect(jsonPath("tokenDto.accessToken").value(tokenDto.getAccessToken()))
                .andExpect(jsonPath("tokenDto.refreshToken").value(tokenDto.getRefreshToken()))
                .andExpect(jsonPath("tokenDto.accessTokenExpiresIn").value(tokenDto.getAccessTokenExpiresIn()))
                .andExpect(jsonPath("tokenDto.refreshTokenExpiresIn").value(tokenDto.getRefreshTokenExpiresIn()))
                .andExpect(jsonPath("memberId").value(1))
                .andExpect(jsonPath("imageFileUrl").value(loginResponse.getImageFileUrl()))
                .andExpect(jsonPath("id").value(loginResponse.getId()))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("id").description("??????????????? ???????????? id"),
                                fieldWithPath("imageFileUrl").description("??????????????? ???????????? ????????? ????????? url"),
                                fieldWithPath("name").description("??????").attributes(field("constraints", "[1,10] ?????? ??????")),
                                fieldWithPath("phone").description("????????? ??????").attributes(field("constraints", "[10,11] ?????? ??????")),
                                fieldWithPath("nickname").description("?????????").attributes(field("constraints", "??????,??????,?????? ?????? [2,20] ??????")),
                                fieldWithPath("age").description("??????").attributes(field("constraints", "[10,100] ??????")),
                                fieldWithPath("sex").description(generateLinkCode(DocUrl.SEX)),
                                fieldWithPath("zoneId").description("?????? id"),
                                fieldWithPath("categoryIdList").description("???????????? ???????????? id ?????????").attributes(field("constraints", "?????? 1???, ?????? 3???")),
                                fieldWithPath("fcmToken").description("FCM ??????"),
                                fieldWithPath("recommendNickname").description("????????? ?????????").optional()
                        ),
                        responseFields(
                                fieldWithPath("memberId").description("?????? Id ???"),
                                fieldWithPath("id").description("??????????????? ???????????? id???"),
                                fieldWithPath("imageFileUrl").description("????????? ?????? url")
                        ).and(tokenDtoDescriptor())
                ))
        ;
    }

    @DisplayName("signupForm @valid")
    @Test
    void sign_up_bean_validation() throws Exception {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .name("??????????????????????????????????????????")
                .phone("01022")
                .nickname("@@@")
                .age(101)
                .sex("E")
                .categoryIdList(List.of(1L,2L,3L,4L))
                .build();

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(10)));
    }

    @DisplayName("signupForm validator ??????????????? ?????? - ????????? ??????")
    @Test
    void sign_up_validator() throws Exception {

        // given
        SignUpRequest signUpRequest = MemberDummy.createWrongInitBinderSignupForm();
        when(memberRepository.existsByNickname(any())).thenReturn(true).thenReturn(false);
        when(memberRepository.existsByPhone(any())).thenReturn(true);
        when(memberRepository.existsByUsername(any())).thenReturn(true);
        when(zoneRepository.existsById(any())).thenReturn(false);
        when(categoryRepository.countCategoryByIdList(any())).thenReturn(4L);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(6)));
    }

    @DisplayName("?????? ?????????")
    @Test
    void reissue_token() throws Exception {

        // Given
        securityUserMockSetting();
        TokenDto tokenDto = createTestTokenDto();
        when(loginService.reissue(any())).thenReturn(tokenDto);

        // Given
        when(loginService.reissue(any())).thenReturn(createTestTokenDto());

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER_REFRESH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("grantType").value("Bearer"))
                .andExpect(jsonPath("accessToken").value(tokenDto.getAccessToken()))
                .andExpect(jsonPath("refreshToken").value(tokenDto.getRefreshToken()))
                .andExpect(jsonPath("accessTokenExpiresIn").value(tokenDto.getAccessTokenExpiresIn()))
                .andExpect(jsonPath("refreshTokenExpiresIn").value(tokenDto.getRefreshTokenExpiresIn()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT refresh token")
                        ),
                        responseFields(
                                fieldWithPath("grantType").type(STRING).description("?????? ??????"),
                                fieldWithPath("accessToken").type(STRING).description("JWT Access Token"),
                                fieldWithPath("refreshToken").type(STRING).description("JWT Refresh Token"),
                                fieldWithPath("accessTokenExpiresIn").type(NUMBER).description("JWT Access Token ?????? ??????"),
                                fieldWithPath("refreshTokenExpiresIn").type(NUMBER).description("JWT Refresh Token ?????? ??????")
                        )
                ));
    }

    @DisplayName("????????????")
    @Test
    void logout() throws Exception {

        // given
        securityUserMockSetting();

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.post(LOGOUT)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .header(REFRESH_TOKEN_HEADER, BEARER_REFRESH_TOKEN))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("RefreshToken").description("JWT refresh token"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token")
                        )
                ));
    }

    @DisplayName("??????????????? ???????????? ??????")
    @Test
    void logout_should_not_access_with_logout_token() throws Exception {
        // given
        when(logoutAccessTokenRedisRepository.existsById(any())).thenReturn(true);

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(LOGOUT)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                .header(REFRESH_TOKEN_HEADER, BEARER_REFRESH_TOKEN))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value("KAKAO-401"));

    }

    @DisplayName("????????? ????????? ???????????? ??????")
    @Test
    void logout_should_not_access_with_withdraw_member() throws Exception {
        // given
        when(jwtTokenUtil.validateToken(any())).thenReturn(true);
        when(logoutAccessTokenRedisRepository.existsById(any())).thenReturn(false);
        when(memberRepository.findByUsername(any())).thenReturn(Optional.empty());

        // when then
        mockMvc.perform(RestDocumentationRequestBuilders.post(LOGOUT)
                .contentType(MediaType.APPLICATION_JSON)
                .header("RefreshToken", "Bearer test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value("MEMBER-401"));

    }

    @DisplayName("request body ????????? ???????????? ??????")
    @Test
    void missing_request_body() throws Exception {

        mockMvc.perform(RestDocumentationRequestBuilders.post(LOGOUT)
                .contentType(MediaType.APPLICATION_JSON)
                .header(REFRESH_TOKEN_HEADER,BEARER_REFRESH_TOKEN)
                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN))
                .andExpect(status().isUnauthorized())
        ;
    }

    @DisplayName("??? ????????? ?????????")
    @Test
    void web_login_director() throws Exception{
        //given
        LoginResponse loginResponse = LoginResponse.createTokenLoginResponse(
                1L,
                createTestTokenDto(),
                KakaoProfile.builder()
                        .id(1L)
                        .properties(KakaoProfile.Properties.builder()
                                .profile_image("profile_image_url")
                                .build())
                        .build()
        );
        when(loginService.webLogin(any(),any())).thenReturn(loginResponse);

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/web/{authority}/login","director")
                .contentType(MediaType.APPLICATION_JSON)
                .header(KAKAO_TOKEN_HEADER, BEARER_KAKAO_TOKEN))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("KakaoAccessToken").description("???????????? Access token")
                        ),
                        pathParameters(
                                parameterWithName("authority").description(generateLinkCode(DocUrl.PATH_AUTHORITY))
                        ),
                        responseFields(
                                fieldWithPath("memberId").description("?????? Id ???"),
                                fieldWithPath("id").description("??????????????? ???????????? id???"),
                                fieldWithPath("imageFileUrl").description("??????????????? ???????????? ????????? ????????? url")
                        ).and(tokenDtoDescriptor())
                ));
    }

    @DisplayName("??? ????????? ?????????")
    @Test
    void web_login_admin() throws Exception{
        //given
        LoginResponse loginResponse = LoginResponse.createTokenLoginResponse(
                1L,
                createTestTokenDto(),
                KakaoProfile.builder()
                        .id(1L)
                        .properties(KakaoProfile.Properties.builder()
                                .profile_image("profile_image_url")
                                .build())
                        .build()
        );
        when(loginService.webLogin(any(),any())).thenReturn(loginResponse);

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/web/{authority}/login","admin")
                .contentType(MediaType.APPLICATION_JSON)
                .header(KAKAO_TOKEN_HEADER, BEARER_KAKAO_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("tokenDto.grantType").value(loginResponse.getTokenDto().getGrantType()))
                .andExpect(jsonPath("tokenDto.accessToken").value(loginResponse.getTokenDto().getAccessToken()))
                .andExpect(jsonPath("tokenDto.refreshToken").value(loginResponse.getTokenDto().getRefreshToken()))
                .andExpect(jsonPath("tokenDto.accessTokenExpiresIn").value(loginResponse.getTokenDto().getAccessTokenExpiresIn()))
                .andExpect(jsonPath("tokenDto.refreshTokenExpiresIn").value(loginResponse.getTokenDto().getRefreshTokenExpiresIn()))
                .andExpect(jsonPath("memberId").value(loginResponse.getMemberId()))
                .andExpect(jsonPath("imageFileUrl").value(loginResponse.getImageFileUrl()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("KakaoAccessToken").description("???????????? Access token")
                        ),
                        pathParameters(
                                parameterWithName("authority").description(generateLinkCode(DocUrl.PATH_AUTHORITY))
                        ),
                        responseFields(
                                fieldWithPath("memberId").description("?????? Id ???"),
                                fieldWithPath("id").description("??????????????? ???????????? id???"),
                                fieldWithPath("imageFileUrl").description("??????????????? ???????????? ????????? ????????? url")
                        ).and(tokenDtoDescriptor())
                ));
    }

    private TokenDto createTestTokenDto() {
        return TokenDto.of("Bearer", "newAccessToken",
                "newRefreshToken", 1000L, 10000L);
    }


    private List<FieldDescriptor> tokenDtoDescriptor() {
        return new ArrayList<>(List.of(
                fieldWithPath("tokenDto.grantType").description("?????? ??????"),
                fieldWithPath("tokenDto.accessToken").description("Access ??????"),
                fieldWithPath("tokenDto.refreshToken").description("Refresh ??????"),
                fieldWithPath("tokenDto.accessTokenExpiresIn").description("Access ?????? ?????? ??????"),
                fieldWithPath("tokenDto.refreshTokenExpiresIn").description("Refresh ?????? ?????? ??????")
        ));
    }
}
