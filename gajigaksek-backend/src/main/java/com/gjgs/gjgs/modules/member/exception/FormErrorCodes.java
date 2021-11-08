package com.gjgs.gjgs.modules.member.exception;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum FormErrorCodes implements ErrorBase {

    INVALID_SIGNUPFORM(HttpStatus.BAD_REQUEST.value(),
            "FORM-400",
            "signupForm에 올바르지 않은 값이 있습니다."),
    INVALID_CATEGORYIDFORM(HttpStatus.BAD_REQUEST.value(),
            "FORM-400",
            "존재하지 않는 categoryId가 있습니다."),
    INVALID_ZONEIDFORM(HttpStatus.BAD_REQUEST.value(),
            "FORM-400",
            "존재하지 않는 zoneId 입니다."),
    SAME_NICKNAME_EXIST(HttpStatus.BAD_REQUEST.value(),
            "FORM-400",
            "닉네임이 중복되었습니다."
    ),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST.value(),
            "FORM-400",
            "형식이 맞지 않습니다."
    ),
    SAME_PHONE_EXIST(HttpStatus.BAD_REQUEST.value(),
            "FORM-400",
            "휴대폰 번호가 중복되었습니다."
    ),
    NOT_EXISTS_ALARM_TYPE(
            HttpStatus.BAD_REQUEST.value(),
            "FORM-400",
            "존재하지 않는 알림 타입 필드입니다."
    )
    ;

    private int status;
    private String code;
    private String message;

    FormErrorCodes(int status, String code, String message) {
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
