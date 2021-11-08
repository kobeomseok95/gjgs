package com.gjgs.gjgs.modules.payment.service.discountpolicy;

import com.gjgs.gjgs.modules.payment.dto.PaymentRequest;
import com.gjgs.gjgs.modules.payment.entity.Order;
import com.gjgs.gjgs.modules.payment.exceptions.PaymentErrorCodes;
import com.gjgs.gjgs.modules.payment.exceptions.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.gjgs.gjgs.modules.payment.service.discountpolicy.DiscountType.*;

@Component
@RequiredArgsConstructor
public class DiscountPolicyDecider {

    private final List<DiscountPolicy> discountPolicyList;

    public DiscountPolicy getDiscountPolicy(PaymentRequest paymentRequest) {
        DiscountType discountType = getDiscountType(paymentRequest);
        return discountPolicyList.stream()
                .filter(discountPolicy -> discountPolicy.getDiscountType().equals(discountType))
                .findFirst()
                .orElseThrow(() -> new PaymentException(PaymentErrorCodes.INVALID_PAY_TYPE));
    }

    private DiscountType getDiscountType(PaymentRequest paymentRequest) {
        Long memberCouponId = paymentRequest.getMemberCouponId();
        int rewardAmount = paymentRequest.getRewardAmount();
        if (memberCouponId == null && rewardAmount == 0) {
            return NOTHING;
        } else if (memberCouponId != null && rewardAmount == 0) {
            return COUPON;
        } else if (memberCouponId == null && rewardAmount > 0) {
            return REWARD;
        } else if (memberCouponId != null && rewardAmount > 0) {
            return COUPON_REWARD;
        }
        throw new PaymentException(PaymentErrorCodes.INVALID_PAY_TYPE);
    }

    public DiscountPolicy getRefundPolicy(Order order) {
        DiscountType refundType = getRefundType(order);
        return discountPolicyList.stream()
                .filter(discountPolicy -> discountPolicy.getDiscountType().equals(refundType))
                .findFirst()
                .orElseThrow(() -> new PaymentException(PaymentErrorCodes.INVALID_REFUND_TYPE));
    }

    private DiscountType getRefundType(Order order) {
        if (order.getReward() == null && order.getMemberCoupon() == null) {
            return NOTHING;
        } else if (order.getMemberCoupon() != null && order.getReward() == null) {
            return COUPON;
        } else if (order.getMemberCoupon() == null && order.getReward() != null) {
            return REWARD;
        } else {
            return COUPON_REWARD;
        }
    }
}
