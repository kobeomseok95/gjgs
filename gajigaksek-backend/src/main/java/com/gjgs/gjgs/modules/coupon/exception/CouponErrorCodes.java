package com.gjgs.gjgs.modules.coupon.exception;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum CouponErrorCodes implements ErrorBase {

    INVALID_COUPON(HttpStatus.BAD_REQUEST.value(),
            "COUPON-400",
            "해당 쿠폰은 사용이 중지되었습니다."),
    AVAILABLE_COUPON(HttpStatus.CONFLICT.value(),
            "COUPON-409",
                    "아직 쿠폰이 발급중입니다. 재발행을 원할 경우 발행을 중지해주세요."),
    COUPON_NOT_FOUND(HttpStatus.BAD_REQUEST.value(),
            "COUPON-404",
            "해당 클래스에 적용할 수 있는 쿠폰이 없습니다."),
    MEMBER_HAS_COUPON(HttpStatus.CONFLICT.value(),
            "COUPON-409",
            "회원이 이미 쿠폰을 갖고 있습니다."),
    NOT_AVAILABLE_COUPON(HttpStatus.BAD_REQUEST.value(),
            "COUPON-400",
            "이미 사용한 쿠폰입니다."),
    NOT_USE_COUPON(HttpStatus.BAD_REQUEST.value(),
            "COUPON-400",
            "사용하지 않은 쿠폰입니다.")
    ;

    private int status;
    private String code;
    private String message;

    CouponErrorCodes(int status, String code, String message) {
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
