package com.gjgs.gjgs.modules.zone.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum ZoneErrorCodes implements ErrorBase {

    ZONE_NOT_FOUND(HttpStatus.BAD_REQUEST.value(),
            "ZONE-400",
            "등록되지 않은 지역입니다.");

    private int status;
    private String code;
    private String message;

    ZoneErrorCodes(int status, String code, String message) {
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
