package com.gjgs.gjgs.modules.utils.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.Errors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessException extends RuntimeException implements ErrorBase {

    private ErrorBase errorCodeBase;
    private Errors errors;

    public BusinessException(String message, ErrorBase errorCodeBase) {
        super(message);
        this.errorCodeBase = errorCodeBase;
    }

    public BusinessException(ErrorBase errorCodeBase) {
        super(errorCodeBase.getMessage());
        this.errorCodeBase = errorCodeBase;
    }

    public BusinessException(String message, ErrorBase errorCode, Errors errors) {
        super(message);
        this.errorCodeBase = errorCode;
        this.errors = errors;
    }

    @Override
    public ErrorBase[] getValues() {
        return errorCodeBase.getValues();
    }

    @Override
    public int getStatus() {
        return errorCodeBase.getStatus();
    }

    @Override
    public String getName() {
        return errorCodeBase.getName();
    }

    @Override
    public String getCode() {
        return errorCodeBase.getCode();
    }
}
