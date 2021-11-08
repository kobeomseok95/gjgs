package com.gjgs.gjgs.modules.utils.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum FileErrorCodes implements ErrorBase {

    MISSING_FILE(HttpStatus.BAD_REQUEST.value(),
            "FILE-101",
            "사진 파일이 존재하지 않습니다.")
    ;


    private int status;
    private String code;
    private String message;

    FileErrorCodes(int status, String code, String message) {
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
