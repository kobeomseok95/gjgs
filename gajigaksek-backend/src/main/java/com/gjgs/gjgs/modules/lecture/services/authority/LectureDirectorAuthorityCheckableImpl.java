package com.gjgs.gjgs.modules.lecture.services.authority;

import com.gjgs.gjgs.modules.lecture.aop.CheckDirector;
import com.gjgs.gjgs.modules.lecture.aop.CheckNotDirector;
import com.gjgs.gjgs.modules.lecture.entity.Lecture;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureErrorCodes;
import com.gjgs.gjgs.modules.lecture.exceptions.LectureException;
import com.gjgs.gjgs.modules.lecture.repositories.lecture.LectureQueryRepository;
import com.gjgs.gjgs.modules.question.entity.Question;
import com.gjgs.gjgs.modules.question.exception.QuestionErrorCodes;
import com.gjgs.gjgs.modules.question.exception.QuestionException;
import com.gjgs.gjgs.modules.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureDirectorAuthorityCheckableImpl implements LectureDirectorAuthorityCheckable {

    private final LectureQueryRepository lectureQueryRepository;
    private final QuestionRepository questionRepository;

    @CheckNotDirector
    @Override
    public Lecture findLecture(Long lectureId) {
        return lectureQueryRepository.findWithDirectorById(lectureId).orElseThrow(() -> new LectureException(LectureErrorCodes.LECTURE_NOT_FOUND));
    }

    @CheckDirector
    @Override
    public Question findQuestion(Long questionId) {
        return questionRepository.findWithLectureDirector(questionId)
                .orElseThrow(() -> new QuestionException(QuestionErrorCodes.NOT_EXIST_QUESTION));
    }
}
