package com.gjgs.gjgs.modules.member.exception;


import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum MemberErrorCodes implements ErrorBase {

    KAKAO_INTERACTION_FAIL(HttpStatus.UNAUTHORIZED.value(),
            "KAKAO-401",
            "카카오에서 사용자 정보를 가져오는데 실패했습니다."), // 카카오 access토큰이 정상적이지 못한 경우
    MEMBER_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(),
            "MEMBER-401",
            "탈퇴했거나 존재하지 않는 회원입니다."),
    MEMBER_EXISTS(HttpStatus.BAD_REQUEST.value(),
            "MEMBER-400",
            "해당 카카오 계정으로 이미 가입된 계정이 존재합니다."),
    MEMBER_NOT_DIRECTOR(HttpStatus.FORBIDDEN.value(),
            "MEMBER-403",
            "회원이 디렉터가 아닙니다."),
    MEMBER_NOT_ADMIN(HttpStatus.FORBIDDEN.value(),
            "MEMBER-403",
            "회원이 어드민이 아닙니다."),
    NOT_EXIST_AUTHORITY(HttpStatus.FORBIDDEN.value(),
            "MEMBER-400",
            "존재하지 않는 Authority 입니다."),
    MEMBER_AUTHORITY_NOT_FOUND(HttpStatus.BAD_REQUEST.value(),
            "MEMBER-400",
            "Security Context 에 해당 회원의 권한이 존재하지 않습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN.value(),
            "MEMBER-403",
            "권한이 옳지 않습니다."),
    NOT_LEADER_OR_DIRECTOR(HttpStatus.FORBIDDEN.value(),
            "MEMBER-403",
                    "해당 컨텐츠를 이용할 수 없습니다.")
    ;


    private int status;
    private String code;
    private String message;

    MemberErrorCodes(int status, String code, String message) {
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
