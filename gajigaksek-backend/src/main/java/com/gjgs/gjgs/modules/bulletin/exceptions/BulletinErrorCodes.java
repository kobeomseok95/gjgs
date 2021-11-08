package com.gjgs.gjgs.modules.bulletin.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum BulletinErrorCodes implements ErrorBase {

    BULLETIN_NOT_FOUND(HttpStatus.NOT_FOUND.value(),
            "BULLETIN-404",
            "해당 모집 게시글이 존재하지 않습니다."),
    BULLETIN_NOT_FOUND_OR_NOT_LEADER(HttpStatus.CONFLICT.value(),
            "BULLETIN-409",
            "해당 모집 게시글이 존재하지 않거나, 그룹장이 아닙니다.")
    ;

    private int status;
    private String code;
    private String message;

    BulletinErrorCodes(int status, String code, String message) {
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
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
