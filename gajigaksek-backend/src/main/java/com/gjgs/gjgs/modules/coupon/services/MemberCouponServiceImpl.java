package com.gjgs.gjgs.modules.coupon.services;

import com.gjgs.gjgs.modules.coupon.dto.EnableMemberCouponResponse;
import com.gjgs.gjgs.modules.coupon.entity.Coupon;
import com.gjgs.gjgs.modules.coupon.exception.CouponErrorCodes;
import com.gjgs.gjgs.modules.coupon.exception.CouponException;
import com.gjgs.gjgs.modules.coupon.repositories.CouponQueryRepository;
import com.gjgs.gjgs.modules.coupon.repositories.MemberCouponQueryRepository;
import com.gjgs.gjgs.modules.coupon.validators.CouponValidator;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.entity.MemberCoupon;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberQueryRepository;
import com.gjgs.gjgs.modules.payment.dto.PaymentRequest;
import com.gjgs.gjgs.modules.utils.jwt.CurrentMemberUtil;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCouponServiceImpl implements MemberCouponService {

    private final CouponQueryRepository couponQueryRepository;
    private final MemberCouponQueryRepository memberCouponQueryRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final SecurityUtil securityUtil;
    private final CurrentMemberUtil currentMemberUtil;
    private final CouponValidator couponValidator;

    @Override
    public void giveMemberCoupon(Long lectureId) {
        Coupon coupon = couponQueryRepository.findByLectureId(lectureId).orElseThrow(() -> new CouponException(CouponErrorCodes.COUPON_NOT_FOUND));
        Member member = memberQueryRepository.findWithCouponByUsername(getCurrentUsernameOrElseThrow()).orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_NOT_FOUND));
        couponValidator.validateDuplicate(member, coupon);
        member.addCoupon(MemberCoupon.of(member, coupon.getDiscountPrice(), coupon.getSerialNumber()));
        coupon.minusCount();
    }

    @Override
    public Member getMemberOrWithCoupon(PaymentRequest paymentRequest) {
        Long memberCouponId = paymentRequest.getMemberCouponId();
        Long lectureId = paymentRequest.getLectureId();

        if (memberCouponId == null) {
            return currentMemberUtil.getCurrentMemberOrThrow();
        }
        return findAndValidateMemberCoupon(memberCouponId, lectureId);
    }

    private Member findAndValidateMemberCoupon(Long memberCouponId, Long lectureId) {
        Member member = memberCouponQueryRepository
                .findByMemberCouponByIdUsername(memberCouponId, getCurrentUsernameOrElseThrow())
                .orElseThrow(() -> new CouponException(CouponErrorCodes.COUPON_NOT_FOUND));
        Coupon coupon = couponQueryRepository.findByLectureId(lectureId).orElseThrow(() -> new CouponException(CouponErrorCodes.COUPON_NOT_FOUND));
        couponValidator.validateAvailableMemberCoupon(member.getMemberCoupon(memberCouponId), coupon);
        return member;
    }

    @Override
    @Transactional(readOnly = true)
    public EnableMemberCouponResponse getMemberCoupon(Long lectureId) {
        return memberCouponQueryRepository.findMyEnableCouponByLectureIdUsername(lectureId, getCurrentUsernameOrElseThrow());
    }

    private String getCurrentUsernameOrElseThrow() {
        return securityUtil.getCurrentUsername()
                .orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_NOT_FOUND));
    }
}
