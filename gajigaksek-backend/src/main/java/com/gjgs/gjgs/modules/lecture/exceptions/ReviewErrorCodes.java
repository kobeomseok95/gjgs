package com.gjgs.gjgs.modules.lecture.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum ReviewErrorCodes implements ErrorBase {

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND.value(),
            "REVIEW-404",
            "해당 리뷰는 존재하지 않습니다."),
    NOT_EXIST_REPLY_REVIEW(HttpStatus.NOT_FOUND.value(),
            "REVIEW-404",
            "답글을 작성할 리뷰가 없습니다."),
    REVIEW_IS_EMPTY(HttpStatus.CONFLICT.value(),
            "REVIEW-409",
            "리뷰가 작성되지 않았습니다."),
    DUPLICATE_REVIEW(HttpStatus.CONFLICT.value(),
            "REVIEW-409",
            "회원은 이미 해당 클래스에 리뷰를 작성하였습니다.")
    ;

    private int status;
    private String code;
    private String message;

    ReviewErrorCodes(int status, String code, String message) {
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
