package com.gjgs.gjgs.modules.payment.service.discountpolicy;

import com.gjgs.gjgs.modules.lecture.entity.Schedule;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.payment.dto.PaymentRequest;
import com.gjgs.gjgs.modules.payment.entity.Order;
import com.gjgs.gjgs.modules.payment.exceptions.PaymentErrorCodes;
import com.gjgs.gjgs.modules.payment.exceptions.PaymentException;

public interface DiscountPolicy {

    default void validApplyPrice(int lecturePrice, PaymentRequest paymentRequest) {
        if (lecturePrice != paymentRequest.getOriginalPrice()) {
            throw new PaymentException(PaymentErrorCodes.INVALID_PRICE);
        }
    }

    DiscountType getDiscountType();

    Long applyPayPersonal(Member member, Schedule schedule, PaymentRequest paymentRequest);

    Long applyPayTeamMember(Member member, Order order, PaymentRequest paymentRequest);

    void refund(Member exitMember, Order order, int refundPrice);
}
