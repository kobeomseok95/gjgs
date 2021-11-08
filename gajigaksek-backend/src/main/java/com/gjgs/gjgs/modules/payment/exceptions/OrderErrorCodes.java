package com.gjgs.gjgs.modules.payment.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum OrderErrorCodes implements ErrorBase{

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND.value(),
            "ORDER-404",
            "존재하지 않는 팀 결제 내역입니다."),
    TEAM_HAS_WAIT_ORDER(HttpStatus.BAD_REQUEST.value(),
            "ORDER-400",
            "결제 진행 중인 클래스가 있을 경우 리더 위임, 팀원 추방, 팀 나가기 및 삭제를 할 수 없습니다."),
    INVALID_PRICE(HttpStatus.CONFLICT.value(),
            "ORDER-409",
            "아임포트에서 결제한 금액과 환불 금액이 일치하지 않아 환불되었습니다."),
    ORDER_IS_CANCEL(HttpStatus.BAD_REQUEST.value(),
            "ORDER-400",
            "해당 결제는 취소되었습니다. 다시 결제해주세요."),
    ORDER_NOT_CANCEL(HttpStatus.BAD_REQUEST.value(),
            "ORDER-400",
            "3일 내로 클래스가 진행될 경우 결제를 취소할 수 없습니다.")
    ;

    private int status;
    private String code;
    private String message;

    OrderErrorCodes(int status, String code, String message) {
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
