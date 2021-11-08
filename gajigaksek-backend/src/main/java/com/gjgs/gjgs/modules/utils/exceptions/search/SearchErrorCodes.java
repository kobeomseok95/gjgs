package com.gjgs.gjgs.modules.utils.exceptions.search;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum SearchErrorCodes implements ErrorBase {

    KEYWORD_IS_NOT_ALLOWED(
            HttpStatus.BAD_REQUEST.value(),
            "SEARCH-400",
            "최소 1글자 이상의 검색어를 입력해주세요.")
    ;

    private int status;
    private String code;
    private String message;

    SearchErrorCodes(int status, String code, String message) {
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
