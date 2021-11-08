package com.gjgs.gjgs.modules.lecture.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleException extends BusinessException {

    public ScheduleException(ErrorBase errorBase) {
        super(errorBase);
    }
}
