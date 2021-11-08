package com.gjgs.gjgs.modules.lecture.services.admin;

import com.gjgs.gjgs.modules.lecture.dtos.admin.DecideLectureType;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class LectureDecideStrategyFactory {

    private final List<DecideLecture> strategies;

    public DecideLecture getStrategy(DecideLectureType type) {
        return strategies
                .stream()
                .filter(strategy -> strategy.getType().equals(type))
                .findFirst()
                .orElseThrow(() -> new LectureException(LectureErrorCodes.INVALID_DECIDE_LECTURE_TYPE));
    }
}
