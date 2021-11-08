package com.gjgs.gjgs.modules.payment.service.pay;

import com.gjgs.gjgs.modules.payment.dto.PayType;
import com.gjgs.gjgs.modules.payment.exceptions.PaymentErrorCodes;
import com.gjgs.gjgs.modules.payment.exceptions.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentServiceFactory {

    private final List<PaymentProcess> paymentProcessList;

    public PaymentProcess getProcess(PayType payType) {
        return paymentProcessList.stream()
                .filter(paymentProcess -> paymentProcess.getType().equals(payType))
                .findFirst()
                .orElseThrow(() -> new PaymentException(PaymentErrorCodes.INVALID_PAY_TYPE));
    }
}
