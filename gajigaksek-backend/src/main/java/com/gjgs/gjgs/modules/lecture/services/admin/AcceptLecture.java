package com.gjgs.gjgs.modules.lecture.services.admin;

import com.gjgs.gjgs.modules.coupon.entity.Coupon;
import com.gjgs.gjgs.modules.coupon.repositories.CouponQueryRepository;
import com.gjgs.gjgs.modules.lecture.dtos.admin.DecideLectureType;
import com.gjgs.gjgs.modules.lecture.dtos.admin.RejectReason;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AcceptLecture implements DecideLecture {

    private final CouponQueryRepository couponQueryRepository;

    @Override
    public DecideLectureType getType() {
        return DecideLectureType.ACCEPT;
    }

    @Override
    public void decide(Long lectureId, RejectReason rejectReason) {
        Coupon coupon = couponQueryRepository.findWithLectureByLectureId(lectureId).orElseThrow(() -> new LectureException(LectureErrorCodes.LECTURE_NOT_FOUND));
        coupon.activateWithLecture();
    }
}
