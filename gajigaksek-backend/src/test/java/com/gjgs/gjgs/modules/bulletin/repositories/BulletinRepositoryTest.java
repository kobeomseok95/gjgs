package com.gjgs.gjgs.modules.bulletin.repositories;

import com.gjgs.gjgs.config.CustomDataJpaTest;
import com.gjgs.gjgs.modules.bulletin.dto.BulletinDetailResponse;
import com.gjgs.gjgs.modules.bulletin.dto.search.BulletinSearchCondition;
import com.gjgs.gjgs.modules.bulletin.dto.search.BulletinSearchResponse;
import com.gjgs.gjgs.modules.bulletin.entity.Bulletin;
import com.gjgs.gjgs.modules.dummy.BulletinDummy;
import com.gjgs.gjgs.modules.dummy.MemberDummy;
import com.gjgs.gjgs.modules.dummy.TeamDummy;
import com.gjgs.gjgs.modules.favorite.entity.BulletinMember;
import com.gjgs.gjgs.modules.favorite.repository.interfaces.BulletinMemberRepository;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberTeamRepository;
import com.gjgs.gjgs.modules.team.dtos.MyLeadTeamsResponse;
import com.gjgs.gjgs.modules.team.entity.MemberTeam;
import com.gjgs.gjgs.modules.team.entity.Team;
import com.gjgs.gjgs.testutils.repository.SetUpLectureTeamBulletinRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

@CustomDataJpaTest
class BulletinRepositoryTest extends SetUpLectureTeamBulletinRepository {

    @Autowired BulletinMemberRepository bulletinMemberRepository;
    @Autowired MemberTeamRepository memberTeamRepository;

    private void set_up_favorite_member() {
        long count = memberRepository.count();
        Member favoriteMember = memberRepository.save(MemberDummy.createDataJpaTestMember((int) (count + 1), zone, category));
        bulletinMemberRepository.save(BulletinMember.of(bulletin, favoriteMember));
        Member favoriteMember2 = memberRepository.save(MemberDummy.createDataJpaTestMember((int) (count + 2), zone, category));
        bulletinMemberRepository.save(BulletinMember.of(bulletin, favoriteMember2));
    }

    @DisplayName("?????? ????????? ????????????")
    @Test
    void search_bulletin_test() throws Exception {

        // given
        flushAndClear();

        BulletinSearchCondition cond = BulletinSearchCondition.builder().build();
        Pageable page = PageRequest.of(0, 10);

        // when
        Page<BulletinSearchResponse> res = bulletinRepository.searchBulletin(page, cond);

        // then
        assertAll(
                () -> assertEquals(res.getContent().size(), 1),
                () -> assertEquals(res.getTotalElements(), 1)
        );
    }

    @DisplayName("?????? ???????????? ???????????? ????????? ??????")
    @Test
    void find_with_picked_bulletins_test() throws Exception {

        // given
        flushAndClear();

        PageRequest page = PageRequest.of(0, 10);

        // when
        Page<BulletinSearchResponse> res = bulletinRepository.findLecturePickBulletins(lecture.getId(), page);

        // then
        assertAll(
                () -> assertEquals(res.getContent().size(), 1),
                () -> assertEquals(res.getTotalElements(), 1)
        );
    }

    @DisplayName("bulletin, team, lecture ????????? ????????????")
    @Test
    void find_with_team_lecture_test() throws Exception {

        // given
        flushAndClear();

        // when
        Bulletin findBulletin = bulletinRepository.findWithLecture(bulletin.getId()).orElseThrow();

        // then
        assertEquals(findBulletin.getId(), bulletin.getId());
    }

    @DisplayName("TeamId??? Bulletin??? ????????????.")
    @Test
    void find_by_team_id_test() throws Exception {

        // when
        Bulletin bulletin = bulletinRepository.findWithTeamByTeamId(team.getId()).orElseThrow();

        // then
        assertEquals(bulletin.getTeam().getId(), team.getId());
    }

    @DisplayName("?????? ?????? ????????? ????????????")
    @Test
    void delete_bulletin_member_test() throws Exception {

        // given
        set_up_favorite_member();
        flushAndClear();

        // when, then
        assertEquals(bulletinRepository.deleteFavoriteBulletinsById(bulletin.getId()), 2);
    }

    @DisplayName("Bulletin ????????? Lecture??? Team ????????? ????????????")
    @Test
    void find_with_lecture_team_test() throws Exception {

        // given
        flushAndClear();

        // when
        Bulletin findBulletin = bulletinRepository.findWithLectureTeam(bulletin.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertNotNull(findBulletin.getTeam()),
                () -> assertNotNull(findBulletin.getLecture())
        );
    }

    @DisplayName("???????????? ???, ?????? ????????? ????????? ????????????")
    @Test
    void bulletin_with_team_leader_test() throws Exception {

        // given
        flushAndClear();

        // when
        Bulletin findBulletin = bulletinRepository.findWithTeamLeaderByBulletinIdLeaderUsername(bulletin.getId(), leader.getUsername())
                .orElseThrow();

        // then
        assertNotNull(findBulletin.getTeam().getLeader());
    }

    @DisplayName("??? ?????? ???????????? ?????????, ????????? ?????? ????????? ???????????? / ????????? ?????????1, ????????? ????????????1, ????????? ??????")
    @Test
    void find_with_lecture_by_member_id_test() throws Exception {

        // given
        teamRepository.save(TeamDummy.createTeam(leader, zone));
        Team teamWithClosedBulletin = teamRepository.save(TeamDummy.createTeam(leader, zone));
        Bulletin closedBulletin = BulletinDummy.createBulletin(teamWithClosedBulletin, lecture);
        closedBulletin.changeRecruitStatus();
        bulletinRepository.save(closedBulletin);
        flushAndClear();

        List<Long> teamIdList = teamRepository.findAll().stream().map(Team::getId).collect(toList());
        MyLeadTeamsResponse response = MyLeadTeamsResponse.builder()
                .myLeadTeams(teamIdList.stream()
                        .map(teamId -> MyLeadTeamsResponse.MyLeadTeamsWithBulletin.builder()
                                .teamId(teamId)
                                .build())
                        .collect(toList()))
                .build();

        // when
        MyLeadTeamsResponse myLeadTeamsResponse = bulletinRepository.findWithLectureByMemberId(response);

        // then
        assertEquals(myLeadTeamsResponse.getMyLeadTeams().size(), 3);
    }

    @DisplayName("????????? ?????? ?????? ????????????")
    @Test
    void find_bulletin_detail() throws Exception {

        // given
        memberTeamRepository.save(MemberTeam.of(anotherMembers.get(0), team));

        // when
        BulletinDetailResponse response = bulletinRepository.findBulletinDetail(bulletin.getId());

        // then
        assertAll(
                () -> assertEquals(response.getBulletinId(), bulletin.getId()),
                () -> assertEquals(response.getBulletinsTeam().getTeamId(), team.getId()),
                () -> assertEquals(response.getBulletinsLecture().getLectureId(), lecture.getId()),
                () -> assertEquals(response.getBulletinsTeam().getMembers().size(), 1)
        );
    }
}
