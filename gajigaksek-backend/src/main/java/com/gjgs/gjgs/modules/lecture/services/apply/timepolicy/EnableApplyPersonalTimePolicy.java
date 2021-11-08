package com.gjgs.gjgs.modules.lecture.services.apply.timepolicy;

import com.gjgs.gjgs.modules.lecture.exceptions.ScheduleErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.ScheduleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Transactional
public class EnableApplyPersonalTimePolicy implements EnableApplyScheduleTimePolicy {

    @Override
    public CheckTimeType getCheckTimeType() {
        return CheckTimeType.PERSONAL;
    }

    @Override
    public void checkCloseTime(LocalDateTime scheduleCloseTime) {
        if (LocalDateTime.now().isAfter(scheduleCloseTime)) {
            throw new ScheduleException(ScheduleErrorCodes.SCHEDULE_OVER_TIME);
        }
    }
}
