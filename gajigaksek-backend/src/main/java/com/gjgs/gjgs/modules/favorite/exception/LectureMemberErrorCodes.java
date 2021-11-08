package com.gjgs.gjgs.modules.favorite.exception;


import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum LectureMemberErrorCodes implements ErrorBase {
    LECTURE_MEMBER_NOT_EXIST(HttpStatus.BAD_REQUEST.value(),
            "LECTURE-MEMBER-400",
            "존재하지 않는 찜 번호입니다."),
    NOT_THE_OWNER_OF_LECTURE_MEMBER(HttpStatus.FORBIDDEN.value(),
            "LECTURE-MEMBER-403",
            "해당 찜 번호의 소유자가 아닙니다.");

    private int status;
    private String code;
    private String message;

    LectureMemberErrorCodes(int status, String code, String message) {
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
