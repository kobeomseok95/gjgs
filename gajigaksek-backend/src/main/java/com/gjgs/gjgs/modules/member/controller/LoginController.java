package com.gjgs.gjgs.modules.member.controller;

import com.gjgs.gjgs.modules.member.dto.login.FcmTokenRequest;
import com.gjgs.gjgs.modules.member.dto.login.LoginResponse;
import com.gjgs.gjgs.modules.member.dto.login.SignUpRequest;
import com.gjgs.gjgs.modules.member.dto.login.TokenDto;
import com.gjgs.gjgs.modules.member.service.login.interfaces.LoginService;
import com.gjgs.gjgs.modules.member.validator.SignUpRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SignUpRequestValidator signUpRequestValidator;

    @InitBinder("signUpRequest")
    public void initBinderSignupForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpRequestValidator);
    }


    /**
     * 로그인
     *
     * @param provider    로그인 제공자 ex) 카카오
     * @param accessToken 로그인 제공자에서 주는 토큰 ex) kakao accessToken
     */
    @PostMapping("/login/{provider}")
    public ResponseEntity<LoginResponse> loginByProvider(@PathVariable String provider,
                                                         @RequestHeader(value = "KakaoAccessToken") String accessToken,
                                                         @RequestBody @Valid FcmTokenRequest fcmTokenRequest) {
        return ResponseEntity.ok(loginService.login(accessToken, fcmTokenRequest.getFcmToken()));
    }


    /**
     * 웹 Director, Admin 로그인
     * @param accessToken JWT
     */
    @PostMapping("/web/{authority}/login")
    public ResponseEntity<LoginResponse> webLogin(@RequestHeader(value = "KakaoAccessToken") String accessToken,
                                                  @PathVariable String authority) {
        return ResponseEntity.ok(loginService.webLogin(accessToken,authority));
    }


    /**
     * 회원가입 + 로그인
     *
     * @param signUpRequest 회원가입 필수 정보
     */
    @PostMapping("/sign-up")
    public ResponseEntity<LoginResponse> firstLogin(@RequestBody @Valid SignUpRequest signUpRequest) {
        return ResponseEntity.ok(loginService.saveAndLogin(signUpRequest));
    }


    /**
     * refresh 토큰 이용한 토큰 재발급
     * refresh 토큰이 7일 이내 만료되는 경우 refresh도 재발급
     *
     * @param refreshToken JWT
     */
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestHeader(value = "Authorization") String refreshToken) {
        return ResponseEntity.ok(loginService.reissue(refreshToken));
    }


    /**
     * 로그아웃
     *
     * @param accessToken  JWT
     * @param refreshToken JWT
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization") String accessToken,
                                       @RequestHeader(value = "RefreshToken") String refreshToken) {

        loginService.logout(accessToken, refreshToken);
        return ResponseEntity.ok().build();
    }



    // todo 반드시 삭제 -> test용
    @PostMapping("/fake/login/{username}")
    public ResponseEntity<LoginResponse> fakeLogin(@PathVariable String username) {
        return ResponseEntity.ok(loginService.fakeLogin(username));
    }
}
