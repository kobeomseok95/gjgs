package com.gjgs.gjgs.modules.lecture.repositories.schedule;

import com.gjgs.gjgs.config.CustomDataJpaTest;
import com.gjgs.gjgs.modules.dummy.ScheduleDummy;
import com.gjgs.gjgs.modules.dummy.TeamDummy;
import com.gjgs.gjgs.modules.lecture.entity.Schedule;
import com.gjgs.gjgs.modules.lecture.repositories.participant.ParticipantJdbcRepository;
import com.gjgs.gjgs.modules.lecture.repositories.participant.ParticipantJdbcRepositoryImpl;
import com.gjgs.gjgs.modules.lecture.repositories.participant.ParticipantRepository;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberTeamRepository;
import com.gjgs.gjgs.modules.team.entity.Team;
import com.gjgs.gjgs.modules.team.repositories.implement.TeamJdbcRepositoryImpl;
import com.gjgs.gjgs.modules.team.repositories.interfaces.TeamJdbcRepository;
import com.gjgs.gjgs.testutils.repository.SetUpLectureTeamBulletinRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import({
        ScheduleQueryRepositoryImpl.class,
        TeamJdbcRepositoryImpl.class,
        ParticipantJdbcRepositoryImpl.class
})
@CustomDataJpaTest
class ScheduleQueryRepositoryImplTest extends SetUpLectureTeamBulletinRepository {

    @Autowired ScheduleRepository scheduleRepository;
    @Autowired TeamJdbcRepository teamJdbcRepository;
    @Autowired ScheduleQueryRepository scheduleQueryRepository;
    @Autowired ParticipantJdbcRepository participantJdbcRepository;
    @Autowired MemberTeamRepository memberTeamRepository;
    @Autowired ParticipantRepository participantRepository;

    @AfterEach
    void tearDown() throws Exception {
        participantRepository.deleteAll();
        memberTeamRepository.deleteAll();
        scheduleRepository.deleteAll();
    }

    @Test
    @DisplayName("????????? ???????????? ?????? ???????????? ?????? ????????????")
    void find_with_participants_by_id_test() throws Exception {

        // given
        Schedule schedule = scheduleRepository.save(ScheduleDummy.createDataJpaTestSchedule(lecture));
        Member member3 = anotherMembers.get(0);
        Member member4 = anotherMembers.get(1);
        Team team = teamRepository.save(TeamDummy.createTeamOfManyMembers(zone, leader, member3, member4));
        teamJdbcRepository.insertMemberTeamList(team.getId(), List.of(member3.getId(), member4.getId()));
        schedule.addParticipants(team.getAllMembers());
        participantJdbcRepository.insertParticipants(schedule);
        flushAndClear();

        // when
        Schedule findSchedule = scheduleQueryRepository.findWithParticipantsById(schedule.getId()).orElseThrow();

        // then
        assertEquals(findSchedule.getParticipantList().size(), team.getAllMembers().size());
    }

    @Test
    @DisplayName("????????? ???????????? ?????? ??????????????? ????????? ?????? ????????????")
    void find_with_lecture_participants_by_id_test() throws Exception {

        // given
        Schedule schedule = scheduleRepository.save(ScheduleDummy.createDataJpaTestSchedule(lecture));
        Member member3 = anotherMembers.get(0);
        Member member4 = anotherMembers.get(1);
        Team team = teamRepository.save(TeamDummy.createTeamOfManyMembers(zone, leader, member3, member4));
        teamJdbcRepository.insertMemberTeamList(team.getId(), List.of(member3.getId(), member4.getId()));
        schedule.addParticipants(team.getAllMembers());
        participantJdbcRepository.insertParticipants(schedule);
        flushAndClear();

        // when
        Schedule findSchedule = scheduleQueryRepository.findWithLectureParticipantsByLectureScheduleId(lecture.getId(), schedule.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertEquals(findSchedule.getParticipantList().size(), 3),
                () -> assertEquals(findSchedule.getLecture().getId(), lecture.getId())
        );
    }
}
