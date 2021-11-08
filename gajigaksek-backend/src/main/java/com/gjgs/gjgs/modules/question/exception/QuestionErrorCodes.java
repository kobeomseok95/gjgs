package com.gjgs.gjgs.modules.question.exception;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum QuestionErrorCodes implements ErrorBase {

    NOT_EXIST_QUESTION(HttpStatus.NOT_FOUND.value(),
            "QUESTION-404",
            "존재하지 않는 문의입니다."),
    NOT_QUESTION_WRITER(HttpStatus.FORBIDDEN.value(),
            "QUESTION-403",
            "해당 문의의 작성자가 아닙니다."),
    QUESTIONER_IS_DIRECTOR(HttpStatus.CONFLICT.value(),
            "QUESTION-409",
            "해당 클래스의 디렉터는 문의글을 작성할 수 없습니다."),
    NOT_DIRECTOR(HttpStatus.FORBIDDEN.value(),
            "QUESTION-403",
            "답글은 해당 클래스의 디렉터만 작성 가능합니다."),

    ;

    private int status;
    private String code;
    private String message;

    QuestionErrorCodes(int status, String code, String message) {
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
