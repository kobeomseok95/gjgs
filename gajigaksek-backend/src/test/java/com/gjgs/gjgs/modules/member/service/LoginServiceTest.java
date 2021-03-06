package com.gjgs.gjgs.modules.member.service;

import com.gjgs.gjgs.modules.dummy.MemberDummy;
import com.gjgs.gjgs.modules.member.dto.login.KakaoProfile;
import com.gjgs.gjgs.modules.member.dto.login.LoginResponse;
import com.gjgs.gjgs.modules.member.dto.login.SignUpRequest;
import com.gjgs.gjgs.modules.member.dto.login.TokenDto;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.enums.Authority;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.member.exception.TokenException;
import com.gjgs.gjgs.modules.member.redis.RefreshToken;
import com.gjgs.gjgs.modules.member.repository.interfaces.LogoutAccessTokenRedisRepository;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberJdbcRepository;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberRepository;
import com.gjgs.gjgs.modules.member.repository.interfaces.RefreshTokenRedisRepository;
import com.gjgs.gjgs.modules.member.service.login.impl.KakaoService;
import com.gjgs.gjgs.modules.member.service.login.impl.LoginServiceImpl;
import com.gjgs.gjgs.modules.utils.jwt.JwtTokenUtil;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock KakaoService kakaoService;
    @Mock MemberRepository memberRepository;
    @Mock JwtTokenUtil jwtTokenUtil;
    @Mock RefreshTokenRedisRepository refreshTokenRedisRepository;
    @Mock LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    @Mock MemberJdbcRepository memberJdbcRepository;
    @Mock SecurityUtil securityUtil;

    @InjectMocks LoginServiceImpl loginService;

    @DisplayName("????????? - ????????? ???????????? ?????? ?????? kakao????????? ????????? ??????")
    @Test
    void login_fail_to_return_kakaoInfo() throws Exception {

        // given
        KakaoProfile kakaoProfile = createKakaoProfile();
        when(kakaoService.getKakaoProfile(any())).thenReturn(kakaoProfile);
        when(memberRepository.findByUsername(any())).thenReturn(Optional.empty());

        //when
        LoginResponse loginResponse = loginService.login("accessToken","fcmToken");

        //then
        assertAll(
                () -> assertNull(loginResponse.getTokenDto()),
                () -> assertNull(loginResponse.getMemberId()),
                () -> assertEquals(loginResponse.getId(), kakaoProfile.getId()),
                () -> assertEquals(loginResponse.getImageFileUrl(), kakaoProfile.getProperties().getProfile_image())
        );
    }

    @DisplayName("????????? - ????????? ???????????? ??????")
    @Test
    void login_success_return_token() throws Exception {

        // given
        Member member = MemberDummy.createMemberForTokenGenerate();
        KakaoProfile kakaoProfile = createKakaoProfile();
        when(kakaoService.getKakaoProfile(any())).thenReturn(kakaoProfile);
        when(memberRepository.findByUsername(any())).thenReturn(Optional.of(member));
        when(jwtTokenUtil.generateTokenDto(member)).thenReturn(MemberDummy.generateToken());

        //when
        LoginResponse loginResponse = loginService.login("accessToken","fcmToken");

        //then
        assertAll(
                () -> assertEquals(kakaoProfile.getId(), loginResponse.getId()),
                () -> assertEquals(kakaoProfile.getProperties().getProfile_image(), loginResponse.getImageFileUrl()),
                () -> assertNotNull(loginResponse.getTokenDto()),
                () -> assertEquals("Bearer", loginResponse.getTokenDto().getGrantType()),
                () -> assertEquals("TestAccessToken", loginResponse.getTokenDto().getAccessToken()),
                () -> assertEquals(1000L * 60 * 360, loginResponse.getTokenDto().getAccessTokenExpiresIn()),
                () -> assertEquals("TestRefreshToken", loginResponse.getTokenDto().getRefreshToken()),
                () -> assertEquals(1000L * 60 * 60 * 24 * 14, loginResponse.getTokenDto().getRefreshTokenExpiresIn())


        );
    }

    @DisplayName("???????????? + ?????????")
    @Test
    void save_and_login() throws Exception {
        //given
        SignUpRequest signUpRequest = MemberDummy.createSignupForm();
        Member member = MemberDummy.createTestMember();
        TokenDto tokenDto = TokenDto.of("Bearer", "AccessToken",
                "RefreshToken", 1L, 1L);

        when(memberRepository.save(any())).thenReturn(member);
        when(jwtTokenUtil.generateTokenDto(ArgumentMatchers.any(Member.class))).thenReturn(tokenDto);

        //when
        LoginResponse loginResponse = loginService.saveAndLogin(signUpRequest);

        //then
        assertAll(
                () -> assertEquals(tokenDto, loginResponse.getTokenDto()),
                () -> assertEquals(signUpRequest.getImageFileUrl(), loginResponse.getImageFileUrl()),
                () -> assertEquals(null, loginResponse.getMemberId()),
                () -> assertEquals(signUpRequest.getId(), loginResponse.getId()),
                () -> verify(memberRepository,times(1)).save(any()),
                () -> verify(memberJdbcRepository,times(1)).insertMemberCategoryList(any(),any()),
                () -> verify(jwtTokenUtil,times(1)).generateTokenDto(ArgumentMatchers.any(Member.class)),
                () -> verify(jwtTokenUtil,times(1)).getRemainingMilliSeconds(any()),
                () -> verify(refreshTokenRedisRepository,times(1)).save(any())
        );
    }

    @DisplayName("?????? ????????? - refresh 7??? ?????? refresh, access ?????? ????????? ")
    @Test
    void reissue() throws Exception {
        //given
        String type = "Bearer";
        String refreshToken = "RefreshToken";
        String newAccessToken = "AccessToken";
        Long remainTime = 1L;
        TokenDto tokenDto = TokenDto.of("Bearer", "newAccessToken",
                "newRefreshToken", 1L, 1L);
        RefreshToken refreshTokenRedis = RefreshToken.createRefreshToken("username", refreshToken, 16L);
        Authentication authentication = MemberDummy.createAuthentication();

        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("test"));
        when(securityUtil.getAuthority()).thenReturn(Optional.of(Authority.ROLE_USER));
        when(jwtTokenUtil.getAuthentication(any(),any())).thenReturn(authentication);
        when(refreshTokenRedisRepository.findById(any())).thenReturn(Optional.of(refreshTokenRedis));
        when(jwtTokenUtil.getRemainingMilliSeconds(any())).thenReturn(remainTime);
        when(jwtTokenUtil.generateTokenDto(ArgumentMatchers.any(Authentication.class))).thenReturn(tokenDto);

        //when
        TokenDto response = loginService.reissue(type + " " + refreshToken);


        //then
        assertAll(
                () -> assertEquals(type, response.getGrantType()),
                () -> assertNotEquals(newAccessToken, response.getAccessToken()),
                () -> assertNotEquals(refreshToken, response.getRefreshToken()),
                () -> assertEquals(remainTime, response.getAccessTokenExpiresIn()),
                () -> assertEquals(remainTime, response.getRefreshTokenExpiresIn())
        );
    }

    @DisplayName("?????? ????????? - refresh 7??? ?????? accessToken??? ???????????? ??????")
    @Test
    void reissue_only_access_token() throws Exception {
        //given
        String type = "Bearer";
        String refreshToken = "RefreshToken";
        String newAccessToken = "newAccessToken";
        Long remainTime = JwtTokenUtil.REFRESH_TOKEN_REISSUE_TIME + 1;
        TokenDto tokenDto = TokenDto.of("Bearer", "newAccessToken",
                "RefreshToken", 1L, 1L);
        RefreshToken refreshTokenRedis = RefreshToken.createRefreshToken("username", refreshToken, 16L);
        Authentication authentication = MemberDummy.createAuthentication();

        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("test"));
        when(securityUtil.getAuthority()).thenReturn(Optional.of(Authority.ROLE_USER));
        when(jwtTokenUtil.getAuthentication(any(),any())).thenReturn(authentication);
        when(refreshTokenRedisRepository.findById(any())).thenReturn(Optional.of(refreshTokenRedis));
        when(jwtTokenUtil.getRemainingMilliSeconds(any())).thenReturn(remainTime);
        when(jwtTokenUtil.generateAccessToken(ArgumentMatchers.any(Authentication.class))).thenReturn(newAccessToken);

        //when
        TokenDto response = loginService.reissue(type + " " + refreshToken);


        //then
        assertAll(
                () -> assertEquals(type, response.getGrantType()),
                () -> assertEquals(newAccessToken, response.getAccessToken()),
                () -> assertEquals(refreshToken, response.getRefreshToken()),
                () -> assertEquals(remainTime, response.getAccessTokenExpiresIn()),
                () -> assertEquals(remainTime, response.getRefreshTokenExpiresIn())
        );
    }

    @DisplayName("redis?????? ????????? refresh??? ?????? refresh??? ?????? ??????")
    @Test
    void invalid_refresh_token() throws Exception {
        //given
        String refreshToken = "Bearer testRefreshToken";
        RefreshToken refreshTokenRedis = RefreshToken.createRefreshToken("username", "fakeRefreshToken", 16L);
        when(jwtTokenUtil.getAuthentication(any(),any())).thenReturn(MemberDummy.createAuthentication());
        when(refreshTokenRedisRepository.findById(any())).thenReturn(Optional.of(refreshTokenRedis));
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("test"));
        when(securityUtil.getAuthority()).thenReturn(Optional.of(Authority.ROLE_USER));

        //when then
        assertThrows(TokenException.class, () -> loginService.reissue(refreshToken));

    }

    @DisplayName("??? ?????????")
    @Test
    void web_login() throws Exception{
        // given
        Member member = MemberDummy.createDirectorMemberForTokenGenerate();
        KakaoProfile kakaoProfile = createKakaoProfile();
        when(kakaoService.getKakaoProfile(any())).thenReturn(kakaoProfile);
        when(memberRepository.findByUsername(any())).thenReturn(Optional.of(member));
        when(jwtTokenUtil.generateTokenDto(member)).thenReturn(MemberDummy.generateToken());

        //when
        LoginResponse loginResponse = loginService.webLogin("accessToken","director");

        //then
        assertAll(
                () -> assertEquals(kakaoProfile.getId(), loginResponse.getId()),
                () -> assertEquals(kakaoProfile.getProperties().getProfile_image(), loginResponse.getImageFileUrl()),
                () -> assertNotNull(loginResponse.getTokenDto()),
                () -> assertEquals("Bearer", loginResponse.getTokenDto().getGrantType()),
                () -> assertEquals("TestAccessToken", loginResponse.getTokenDto().getAccessToken()),
                () -> assertEquals(1000L * 60 * 360, loginResponse.getTokenDto().getAccessTokenExpiresIn()),
                () -> assertEquals("TestRefreshToken", loginResponse.getTokenDto().getRefreshToken()),
                () -> assertEquals(1000L * 60 * 60 * 24 * 14, loginResponse.getTokenDto().getRefreshTokenExpiresIn())
        );
    }

    @DisplayName("?????? ????????? ????????? ????????? ??? ?????? ?????? ??????")
    @Test
    void web_login_by_not_director() throws Exception{
        // given
        Member member = MemberDummy.createMemberForTokenGenerate();
        KakaoProfile kakaoProfile = createKakaoProfile();
        when(kakaoService.getKakaoProfile(any())).thenReturn(kakaoProfile);
        when(memberRepository.findByUsername(any())).thenReturn(Optional.of(member));

        // when then
        assertThrows(MemberException.class,
                () -> loginService.webLogin("accessToken","director"));
    }


    @DisplayName("????????????")
    @Test
    void logout() throws Exception{
        //given
        String token ="Bearer testToken";
        Member member = MemberDummy.createTestMember();
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of(member.getUsername()));
        when(memberRepository.findByUsername(any())).thenReturn(Optional.of(member));
        when(jwtTokenUtil.getRemainingMilliSeconds(any())).thenReturn(10L);

        //when
        loginService.logout(token,token);

        //then
        assertAll(
                () -> verify(refreshTokenRedisRepository).deleteById(member.getUsername()),
                () -> verify(logoutAccessTokenRedisRepository).save(any())
        );

    }

    private KakaoProfile createKakaoProfile() {
        KakaoProfile.Properties properties = KakaoProfile.Properties.builder()
                .nickname("test")
                .profile_image("test_profile_image")
                .thumbnail_image("test_thumbnail_image")
                .build();
        return KakaoProfile.builder()
                .id(1L)
                .properties(properties)
                .build();
    }

}

