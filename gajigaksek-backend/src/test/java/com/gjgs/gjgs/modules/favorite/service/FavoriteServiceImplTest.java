package com.gjgs.gjgs.modules.favorite.service;


import com.gjgs.gjgs.modules.dummy.LectureDummy;
import com.gjgs.gjgs.modules.dummy.MemberDummy;
import com.gjgs.gjgs.modules.dummy.TeamDummy;
import com.gjgs.gjgs.modules.favorite.dto.LectureMemberDto;
import com.gjgs.gjgs.modules.favorite.dto.LectureTeamDto;
import com.gjgs.gjgs.modules.favorite.dto.MyTeamAndIsIncludeFavoriteLectureDto;
import com.gjgs.gjgs.modules.favorite.entity.LectureTeam;
import com.gjgs.gjgs.modules.favorite.repository.interfaces.*;
import com.gjgs.gjgs.modules.favorite.service.impl.FavoriteServiceImpl;
import com.gjgs.gjgs.modules.lecture.embedded.Price;
import com.gjgs.gjgs.modules.lecture.entity.Lecture;
import com.gjgs.gjgs.modules.lecture.repositories.lecture.LectureRepository;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberRepository;
import com.gjgs.gjgs.modules.team.entity.Team;
import com.gjgs.gjgs.modules.team.repositories.interfaces.TeamQueryRepository;
import com.gjgs.gjgs.modules.team.repositories.interfaces.TeamRepository;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @InjectMocks FavoriteServiceImpl favoriteService;

    @Mock MemberRepository memberRepository;
    @Mock LectureMemberRepository lectureMemberRepository;
    @Mock LectureMemberQueryRepository lectureMemberQueryRepository;
    @Mock TeamRepository teamRepository;
    @Mock LectureTeamQueryRepository lectureTeamQueryRepository;
    @Mock BulletinMemberRepository bulletinMemberRepository;
    @Mock LectureRepository lectureRepository;
    @Mock LectureTeamRepository lectureTeamRepository;
    @Mock TeamQueryRepository teamQueryRepository;
    @Mock SecurityUtil securityUtil;
    @Mock BulletinMemberQueryRepository bulletinMemberQueryRepository;

    @DisplayName("?????? ?????? ????????????")
    @Test
    void get_my_favoriteLectures() throws Exception {
        //given
        LectureMemberDto lectureMemberDto = LectureMemberDto.builder()
                .lectureMemberId(1L)
                .lectureId(1L)
                .thumbnailImageFileUrl("test")
                .zoneId(1L)
                .title("test")
                .price(Price.builder()
                        .priceOne(50000)
                        .priceTwo(40000)
                        .priceThree(30000)
                        .priceFour(20000)
                        .build())
                .build();

        when(lectureMemberQueryRepository.findNotFinishedLectureByUsername(any())).thenReturn(Arrays.asList(lectureMemberDto));
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("test"));

        //when
        List<LectureMemberDto> dtos = favoriteService.getMyFavoriteLectures();
        LectureMemberDto dto = dtos.get(0);

        //then
        assertAll(
                () -> assertEquals(1, dtos.size()),
                () -> assertEquals(lectureMemberDto, dto)
        );

    }

    @DisplayName("?????? ?????? ?????? ??????")
    @Test
    void get_teamFavoriteLectures() throws Exception {
        //given
        Member member = MemberDummy.createTestMember();
        Team team = TeamDummy.createUniqueTeam();
        team.addTeamMember(member);
        LectureTeamDto lectureTeamDto = LectureTeamDto.builder()
                .lectureTeamId(1L)
                .lectureId(1L)
                .thumbnailImageFileUrl("test")
                .zoneId(1L)
                .title("test")
                .price(Price.builder()
                        .priceOne(50000)
                        .priceTwo(40000)
                        .priceThree(30000)
                        .priceFour(20000)
                        .build())
                .build();
        when(teamRepository.findWithMembersAndLeaderById(any())).thenReturn(Optional.of(team));
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of(member.getUsername()));

        //when
        favoriteService.getTeamFavoriteLectures(1L);

        //then
        assertAll(
                () -> verify(teamRepository,times(1)).findWithMembersAndLeaderById(any()),
                () -> verify(securityUtil,times(1)).getCurrentUsername(),
                () -> verify(lectureTeamQueryRepository,times(1)).findNotFinishedLectureByTeamId(any())
        );
    }

    @DisplayName("?????? ?????? ?????? ??????")
    @Test
    void delete_teamFavoriteLecture() throws Exception {
        //given
        Member member = MemberDummy.createTestMember();
        Team team = TeamDummy.createUniqueTeam();
        team.addTeamMember(member);
        Lecture lecture = LectureDummy.createLecture(1);
        LectureTeam lectureTeam = LectureTeam.builder()
                .id(1L)
                .lecture(lecture)
                .team(team)
                .build();

        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of(member.getUsername()));
        when(teamRepository.findWithMembersAndLeaderById(any())).thenReturn(Optional.of(team));
        when(lectureTeamRepository.findIdByLectureIdAndTeamId(any(),any())).thenReturn(Optional.of(1L));

        //when
        favoriteService.deleteTeamFavoriteLecture(1L, 1L);

        //then
        assertAll(
                () -> verify(securityUtil,times(1)).getCurrentUsername(),
                () -> verify(teamRepository,times(1)).findWithMembersAndLeaderById(any()),
                () -> verify(lectureTeamRepository,times(1)).findIdByLectureIdAndTeamId(any(),any()),
                () -> verify(lectureTeamRepository,times(1)).deleteById(any())
        );
    }

    @DisplayName("?????? ?????? ????????? ????????????")
    @Test
    void get_my_favoriteBulletins() throws Exception {
        //given
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("test"));

        //when
        favoriteService.getMyFavoriteBulletins();

        //then
        verify(bulletinMemberQueryRepository,times(1)).findBulletinMemberDtoByUsername(any());
    }

    @DisplayName("?????? ?????? ????????? ??????")
    @Test
    void delete_my_favoriteBulletin() throws Exception {
        //given
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("test"));
        when(bulletinMemberQueryRepository.findIdByBulletinIdAndUsername(any(),any())).thenReturn(Optional.of(1L));

        //when
        favoriteService.deleteMyFavoriteBulletin( 1L);

        //then
        assertAll(
                () -> verify(securityUtil,times(1)).getCurrentUsername(),
                () -> verify(bulletinMemberQueryRepository,times(1)).findIdByBulletinIdAndUsername(any(),any()),
                () -> verify(bulletinMemberRepository,times(1)).deleteById(any())
        );
    }


    @DisplayName("?????? ?????????")
    @Test
    void save_my_favoriteLecture() throws Exception {
        //given
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("test"));
        when(lectureRepository.existsLectureByIdAndLectureStatus(any(),any())).thenReturn(true);
        when(memberRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(lectureMemberRepository.findByMemberIdAndLectureId(any(),any())).thenReturn(Optional.empty());

        //when
        favoriteService.saveMyFavoriteLecture(1L);

        //then
        assertAll(
                () -> verify(lectureRepository,times(1)).existsLectureByIdAndLectureStatus(any(),any()),
                () -> verify(memberRepository,times(1)).findIdByUsername(any()),
                () -> verify(lectureMemberRepository,times(1)).findByMemberIdAndLectureId(any(),any()),
                () -> verify(lectureMemberRepository,times(1)).save(any())
        );
    }

    @DisplayName("??? ?????? ?????????")
    @Test
    void save_teamFavoriteLecture() throws Exception {
        //given
        Member member = MemberDummy.createTestMember();
        Team team = TeamDummy.createUniqueTeam();
        team.addTeamMember(member);

        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of(member.getUsername()));
        when(teamRepository.findWithMembersAndLeaderById(any())).thenReturn(Optional.of(team));
        when(lectureRepository.existsLectureByIdAndLectureStatus(any(),any())).thenReturn(true);
        when(lectureTeamRepository.findIdByLectureIdAndTeamId(any(), any())).thenReturn(Optional.empty());

        //when
        favoriteService.saveTeamFavoriteLecture(1L, 1L);

        //then
        assertAll(
                () -> verify(securityUtil,times(1)).getCurrentUsername(),
                () -> verify(teamRepository,times(1)).findWithMembersAndLeaderById(any()),
                () -> verify(lectureRepository,times(1)).existsLectureByIdAndLectureStatus(any(),any()),
                () -> verify(lectureTeamRepository,times(1)).findIdByLectureIdAndTeamId(any(),any())
        );
    }

    @DisplayName("????????? ?????????")
    @Test
    void save_my_favoriteBulletin() throws Exception {
        //given
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("test"));
        when(memberRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(bulletinMemberQueryRepository.existsByBulletinIdAndBulletinStatusAndMemberId(any(),ArgumentMatchers.anyBoolean(),any())).thenReturn(false);


        //when
        favoriteService.saveMyFavoriteBulletin(1L);

        //then
        assertAll(
                () -> verify(securityUtil,times(1)).getCurrentUsername(),
                () -> verify(memberRepository,times(1)).findIdByUsername(any()),
                () -> verify(bulletinMemberQueryRepository,times(1)).existsByBulletinIdAndBulletinStatusAndMemberId(any(),ArgumentMatchers.anyBoolean(),any()),
                () -> verify(bulletinMemberRepository,times(1)).save(any())
        );
    }

    @DisplayName("?????? ?????? ?????? ?????? ????????????")
    @Test
    void get_my_teamAndIsIncludeFavoriteLecture() throws Exception {
        //given
        Member member = MemberDummy.createTestMember();
        Team team = Team.builder()
                .id(1L)
                .build();


        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of(member.getUsername()));
        when(teamQueryRepository.findMyAllTeamByUsername(any())).thenReturn(Arrays.asList(team));
        when(lectureTeamQueryRepository.findTeamByLectureIdAndTeamIdList(any(), any())).thenReturn(Arrays.asList(1L));

        //when
        List<MyTeamAndIsIncludeFavoriteLectureDto> dtos = favoriteService.getMyTeamAndIsIncludeFavoriteLecture(1L);
        MyTeamAndIsIncludeFavoriteLectureDto dto = dtos.get(0);

        //then
        assertAll(
                () -> assertEquals(1, dtos.size()),
                () -> assertEquals(1, dto.getTeamId()),
                () -> assertTrue(dto.isInclude()),
                () -> assertTrue(dto instanceof MyTeamAndIsIncludeFavoriteLectureDto)
        );

    }
}

