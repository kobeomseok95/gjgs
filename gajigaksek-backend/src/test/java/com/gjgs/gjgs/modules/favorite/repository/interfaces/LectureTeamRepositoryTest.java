package com.gjgs.gjgs.modules.favorite.repository.interfaces;


import com.gjgs.gjgs.config.CustomDataJpaTest;
import com.gjgs.gjgs.modules.favorite.entity.LectureTeam;
import com.gjgs.gjgs.testutils.repository.SetUpLectureTeamBulletinRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CustomDataJpaTest
class LectureTeamRepositoryTest extends SetUpLectureTeamBulletinRepository {

    @Autowired LectureTeamRepository lectureTeamRepository;

    @AfterEach
    void tearDown() throws Exception {
        lectureTeamRepository.deleteAll();
    }

    @DisplayName("lectureId와 TeamId로 LectureTeam 찾기")
    @Test
    void find_by_lectureId_and_teamId() throws Exception {

        // given
        LectureTeam lectureTeam = lectureTeamRepository.save(LectureTeam.of(lecture, team));

        flushAndClear();

        //when
        Long lectureTeamId = lectureTeamRepository.findIdByLectureIdAndTeamId(lecture.getId(), team.getId()).get();

        //then
        assertEquals(lectureTeam.getId(),lectureTeamId);
    }
}
