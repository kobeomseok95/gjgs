package com.gjgs.gjgs.modules.member.exception;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum TokenErrorCodes implements ErrorBase {


    TOKEN_TYPE_IS_NOT_BEARER(HttpStatus.UNAUTHORIZED.value(),
            "KAKAO-401",
            "Token Type이 Bearer이 아닙니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(),
            "KAKAO-401",
            "정상적이지 않은 토큰입니다."),
    AUTHORITY_KEY_NULL(HttpStatus.UNAUTHORIZED.value(),
            "KAKAO-401",
            "권한 정보가 없는 토큰입니다."),

    NO_AUTHORIZATION_TOKEN(HttpStatus.UNAUTHORIZED.value(),
            "TOKEN-401",
            "헤더에 Authorization 토큰이 없습니다."),
    NO_KAKAO_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED.value(),
            "KAKAO-401",
            "헤더에 KakaoAccessToken 토큰이 없습니다."),
    NO_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(),
            "TOKEN-401",
            "헤더에 RefreshToken 토큰이 없습니다."),
    REFRESH_TOKEN_NOT_EXIST_IN_REDIS(HttpStatus.BAD_REQUEST.value(),
            "TOKEN-400",
            "REDIS에 해당 refresh 토큰이 존재하지 않습니다."),

    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(),
            "TOKEN-401",
            "만료된 토큰입니다.");

    private int status;
    private String code;
    private String message;

    TokenErrorCodes(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public ErrorBase[] getValues() {
        return values();
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getName() {
        return name();
    }
}
