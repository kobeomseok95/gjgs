package com.gjgs.gjgs.modules.lecture.repositories.impl;

import com.gjgs.gjgs.config.CustomJdbcBatchTest;
import com.gjgs.gjgs.modules.dummy.LectureDummy;
import com.gjgs.gjgs.modules.lecture.entity.Curriculum;
import com.gjgs.gjgs.modules.lecture.entity.FinishedProduct;
import com.gjgs.gjgs.modules.lecture.entity.Lecture;
import com.gjgs.gjgs.modules.lecture.entity.Schedule;
import com.gjgs.gjgs.modules.lecture.repositories.lecture.LectureJdbcRepository;
import com.gjgs.gjgs.modules.lecture.repositories.lecture.LectureJdbcRepositoryImpl;
import com.gjgs.gjgs.modules.lecture.repositories.lecture.LectureRepository;
import com.gjgs.gjgs.testutils.repository.SetUpMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CustomJdbcBatchTest
@Import(LectureJdbcRepositoryImpl.class)
class LectureJdbcRepositoryImplTest extends SetUpMemberRepository {

    @Autowired LectureJdbcRepository lectureJdbcRepository;
    @Autowired LectureRepository lectureRepository;

    @Test
    @DisplayName("2개의 스케줄 저장하기 bulk")
    void insert2_schedules_test_with_lecture() throws Exception {

        // given
        Lecture lecture = lectureRepository.save(LectureDummy.createJdbcTestConfirmLecture(zone, category, director));

        // when
        lectureJdbcRepository.insertSchedule(lecture);
        List<Schedule> scheduleList = lectureRepository.findById(lecture.getId()).orElseThrow().getScheduleList();

        // then
        assertEquals(scheduleList.size(), 2);
    }

    @Test
    @DisplayName("3개의 커리큘럼 저장하기 bulk")
    void insert3_curriculums_test_with_lecture() throws Exception {

        // given
        Lecture lecture = lectureRepository.save(LectureDummy.createJdbcTestConfirmLecture(zone, category, director));

        // when
        lectureJdbcRepository.insertSchedule(lecture);
        List<Curriculum> curriculumList = lectureRepository.findById(lecture.getId()).orElseThrow().getCurriculumList();

        // then
        assertEquals(curriculumList.size(), 3);
    }

    @Test
    @DisplayName("4개의 완성작 저장하기 bulk")
    void insert4_finished_product_test_with_lecture() throws Exception {

        // given
        Lecture lecture = lectureRepository.save(LectureDummy.createJdbcTestConfirmLecture(zone, category, director));

        // when
        lectureJdbcRepository.insertSchedule(lecture);
        List<FinishedProduct> finishedProductList = lectureRepository.findById(lecture.getId()).orElseThrow().getFinishedProductList();

        // then
        assertEquals(finishedProductList.size(), 4);
    }
}
