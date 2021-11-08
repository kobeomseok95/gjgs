package com.gjgs.gjgs.modules.utils.response;

import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@NoArgsConstructor // (access = AccessLevel.PROTECTED) advicecontroller에서 사용하기 위해 주석 처리
public class ErrorResponse {

    private LocalDateTime time;

    private int status;

    private String message;

    private String code;

    private List<FieldError> errors;

    private ErrorResponse(ErrorBase errorBase, List<FieldError> errors) {
        this.time = LocalDateTime.now();
        this.status = errorBase.getStatus();
        this.message = errorBase.getMessage();
        this.code = errorBase.getCode();
        this.errors = errors;
    }

    private ErrorResponse(ErrorBase errorBase, List<FieldError> errors, String message) {
        this.time = LocalDateTime.now();
        this.code = errorBase.getCode();
        this.status = errorBase.getStatus();
        this.errors = errors;
        this.message = message;
    }

    private ErrorResponse(ErrorBase errorBase) {
        this.time = LocalDateTime.now();
        this.status = errorBase.getStatus();
        this.message = errorBase.getMessage();
        this.code = errorBase.getCode();
        this.errors = new ArrayList<>();
    }

    private ErrorResponse(BindingResult bindingResult) {
        this.time = LocalDateTime.now();
        this.status = HttpStatus.BAD_REQUEST.value();
        this.message = "요청에 대한 조건이 맞지 않습니다.";
        this.code = "VALIDATE";
        this.errors = FieldError.of(bindingResult);
    }

    private ErrorResponse(String message) {
        this.time = LocalDateTime.now();
        this.status = HttpStatus.BAD_REQUEST.value();
        this.message = message;
        this.code = "VALIDATE";
    }

    private ErrorResponse(int status, String message) {
        this.time = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.code = MemberErrorCodes.FORBIDDEN.getCode();
    }

    public static ErrorResponse of(final ErrorBase errorBase, List<FieldError> errors) {
        return new ErrorResponse(errorBase, errors);
    }

    public static ErrorResponse of(final ErrorBase errorBase, final Errors errors, String message) {
        return new ErrorResponse(errorBase, FieldError.of(errors), message);
    }

    public static ErrorResponse of(final ErrorBase errorBase) {
        return new ErrorResponse(errorBase);
    }

    public static ErrorResponse of(final ErrorBase errorBase, Errors errors) {
        return new ErrorResponse(errorBase, FieldError.of(errors));
    }

    public static ErrorResponse of(BindingResult bindingResult) {
        return new ErrorResponse(bindingResult);
    }

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }

    public static ErrorResponse ofForbidden(String message) {
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(), message);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class FieldError {
        private String field;

        private String value;

        private String reason;

        private FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }


        private static List<FieldError> of(final Errors errors) {
            // 여기 수정
            List<org.springframework.validation.FieldError> fieldErrors = errors.getFieldErrors();
            if (fieldErrors.isEmpty()) {
                return new ArrayList<>();
            }
            return fieldErrors.stream().map(error -> new FieldError(
                    error.getField(),
                    error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                    error.getDefaultMessage())).collect(toList());
        }

        private static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(toList());
        }
    }
}
