package com.gjgs.gjgs.modules.lecture.services.apply.timepolicy;

import com.gjgs.gjgs.modules.lecture.exceptions.ScheduleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

class EnableApplyPersonalTimePolicyTest {

    @Test
    @DisplayName("개인 신청의 경우는 시작 시간이 지났을 때 신청할 수 없다.")
    void check_close_time_exception_test() throws Exception {

        // given
        EnableApplyPersonalTimePolicy policy = new EnableApplyPersonalTimePolicy();
        LocalDateTime scheduleStartTime = LocalDateTime.now().minusSeconds(1);

        // when, then
        assertThrows(ScheduleException.class,
                () -> policy.checkCloseTime(scheduleStartTime),
                "개인 신청은 시작 시간 전까지 신청 가능하다");
    }
}