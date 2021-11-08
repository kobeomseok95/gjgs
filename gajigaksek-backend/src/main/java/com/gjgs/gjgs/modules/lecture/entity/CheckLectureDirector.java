package com.gjgs.gjgs.modules.lecture.entity;

import com.gjgs.gjgs.modules.lecture.exceptions.LectureErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureException;
import com.gjgs.gjgs.modules.member.entity.Member;

public interface CheckLectureDirector {

    default void checkNotDirector(Member director, Member target) {
        if (isDirector(director, target)) {
            throw new LectureException(LectureErrorCodes.INVALID_ACTION_CAUSE_I_AM_DIRECTOR);
        }
    }

    private boolean isDirector(Member director, Member target) {
        if (target.getId() != null) {
            return director.getId().equals(target.getId());
        }
        return director.getUsername().equals(target.getUsername());
    }
}
