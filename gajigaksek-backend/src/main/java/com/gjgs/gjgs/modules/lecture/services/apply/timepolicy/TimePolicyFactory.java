package com.gjgs.gjgs.modules.lecture.services.apply.timepolicy;

import com.gjgs.gjgs.modules.lecture.exceptions.LectureErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class TimePolicyFactory {

    private final List<EnableApplyScheduleTimePolicy> policyList;

    public EnableApplyScheduleTimePolicy getPolicy(CheckTimeType timeType) {
        return policyList.stream()
                .filter(policy -> policy.getCheckTimeType().equals(timeType))
                .findFirst()
                .orElseThrow(() -> new LectureException(LectureErrorCodes.INVALID_APPLY_TYPE));
    }
}
