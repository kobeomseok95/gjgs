package com.gjgs.gjgs.modules.coupon.validators;

import com.gjgs.gjgs.modules.coupon.entity.Coupon;
import com.gjgs.gjgs.modules.coupon.exception.CouponErrorCodes;
import com.gjgs.gjgs.modules.coupon.exception.CouponException;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.entity.MemberCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CouponValidator {

    public void validateDuplicate(Member couponReceiver, Coupon coupon) {
        checkMemberHasCoupon(couponReceiver, coupon.getSerialNumber());
        checkCouponAvailable(coupon);
    }

    private void checkMemberHasCoupon(Member couponReceiver, String serialNumber) {
        couponReceiver.getCoupons().forEach(memberCoupon -> {
            if (memberCoupon.getSerialNumber().equals(serialNumber)) {
                throw new CouponException(CouponErrorCodes.MEMBER_HAS_COUPON);
            }
        });
    }

    public void validateAvailableMemberCoupon(MemberCoupon memberCoupon, Coupon coupon) {
        checkCouponAvailable(coupon);
        checkSerialNumber(memberCoupon, coupon);
    }

    private void checkCouponAvailable(Coupon coupon) {
        if (!coupon.isAvailable()) {
            throw new CouponException(CouponErrorCodes.INVALID_COUPON);
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getIssueDate()) || now.isAfter(coupon.getCloseDate())) {
            throw new CouponException(CouponErrorCodes.INVALID_COUPON);
        }
    }

    private void checkSerialNumber(MemberCoupon memberCoupon, Coupon coupon) {
        if (!memberCoupon.getSerialNumber().equals(coupon.getSerialNumber())) {
            throw new CouponException(CouponErrorCodes.INVALID_COUPON);
        }
    }
}
