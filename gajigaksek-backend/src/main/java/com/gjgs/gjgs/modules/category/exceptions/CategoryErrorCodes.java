package com.gjgs.gjgs.modules.category.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum CategoryErrorCodes implements ErrorBase {

    CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST.value(),
            "CATEGORY-400",
            "등록되지 않은 취미 카테고리입니다.");

    private int status;
    private String code;
    private String message;

    CategoryErrorCodes(int status, String code, String message) {
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
