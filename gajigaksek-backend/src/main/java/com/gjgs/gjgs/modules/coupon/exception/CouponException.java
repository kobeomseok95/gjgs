package com.gjgs.gjgs.modules.coupon.exception;

import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponException extends BusinessException {

    public CouponException(ErrorBase errorBase) {
        super(errorBase);
    }
}
