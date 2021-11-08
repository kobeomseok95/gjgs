package com.gjgs.gjgs.modules.payment.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum PaymentErrorCodes implements ErrorBase{

    INVALID_PAY_TYPE(HttpStatus.BAD_REQUEST.value(),
            "PAYMENT-404",
            "존재하지 않는 결제 유형입니다."),
    INVALID_PRICE(HttpStatus.BAD_REQUEST.value(),
            "PAYMENT-400",
            "결제 금액이 맞지 않습니다."),
    INVALID_REFUND_TYPE(HttpStatus.BAD_REQUEST.value(),
            "PAYMENT-400",
            "결제했던 할인 정보들이 일치하지 않습니다.")
    ;

    private int status;
    private String code;
    private String message;

    PaymentErrorCodes(int status, String code, String message) {
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
