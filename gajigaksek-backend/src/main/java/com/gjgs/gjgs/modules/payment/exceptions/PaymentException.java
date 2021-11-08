package com.gjgs.gjgs.modules.payment.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentException extends BusinessException {

    public PaymentException(ErrorBase errorBase) {
        super(errorBase.getMessage(),errorBase);
    }
}
