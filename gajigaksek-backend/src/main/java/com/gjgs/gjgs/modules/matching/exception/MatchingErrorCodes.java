package com.gjgs.gjgs.modules.matching.exception;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum MatchingErrorCodes implements ErrorBase {

    ALREADY_EXIST(HttpStatus.BAD_REQUEST.value(),
            "MATCH-400",
            "이미 매칭이 진행중인 회원입니다."),
    MATCHING_FORM_ERROR(HttpStatus.BAD_REQUEST.value(),
            "MATCH-400",
            "매칭 폼에 적절하지 못한 값이 입력되었습니다.");


    private int status;
    private String code;
    private String message;

    MatchingErrorCodes(int status, String code, String message) {
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
