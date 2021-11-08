package com.gjgs.gjgs.modules.utils.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum HeaderErrorCodes implements ErrorBase {

    MISSING_REQUEST_HEADER(HttpStatus.BAD_REQUEST.value(),
            "HEADER-100",
            "특정 헤더값이 들어오지 않았습니다."
    );

    private int status;
    private String code;
    private String message;

    HeaderErrorCodes(int status, String code, String message) {
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
