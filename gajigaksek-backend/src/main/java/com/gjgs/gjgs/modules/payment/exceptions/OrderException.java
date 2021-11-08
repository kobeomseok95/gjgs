package com.gjgs.gjgs.modules.payment.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class OrderException extends BusinessException {

    public OrderException(ErrorBase errorBase) {
        super(errorBase.getMessage(),errorBase);
    }
}
