package com.gjgs.gjgs.modules.matching.service;


import com.gjgs.gjgs.modules.category.entity.Category;
import com.gjgs.gjgs.modules.dummy.CategoryDummy;
import com.gjgs.gjgs.modules.dummy.MemberDummy;
import com.gjgs.gjgs.modules.dummy.ZoneDummy;
import com.gjgs.gjgs.modules.matching.dto.MatchingRequest;
import com.gjgs.gjgs.modules.matching.dto.MatchingStatusResponse;
import com.gjgs.gjgs.modules.matching.enums.Status;
import com.gjgs.gjgs.modules.matching.event.MatchingCompleteEvent;
import com.gjgs.gjgs.modules.matching.repository.interfaces.MatchingQueryRepository;
import com.gjgs.gjgs.modules.matching.repository.interfaces.MatchingRepository;
import com.gjgs.gjgs.modules.matching.service.impl.MatchingServiceImpl;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberQueryRepository;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberRepository;
import com.gjgs.gjgs.modules.notification.dto.MemberFcmIncludeNicknameDto;
import com.gjgs.gjgs.modules.team.entity.Team;
import com.gjgs.gjgs.modules.team.repositories.interfaces.TeamCategoryRepository;
import com.gjgs.gjgs.modules.team.repositories.interfaces.TeamJdbcRepository;
import com.gjgs.gjgs.modules.team.repositories.interfaces.TeamRepository;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import com.gjgs.gjgs.modules.zone.entity.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @InjectMocks MatchingServiceImpl matchingService;

    @Mock MatchingRepository matchingRepository;
    @Mock MatchingQueryRepository matchingQueryRepository;
    @Mock TeamRepository teamRepository;
    @Mock TeamJdbcRepository teamJdbcRepository;
    @Mock TeamCategoryRepository teamCategoryRepository;
    @Mock SecurityUtil securityUtil;
    @Mock ApplicationEventPublisher eventPublisher;
    @Mock MemberQueryRepository memberQueryRepository;
    @Mock MemberRepository memberRepository;


    MatchingRequest matchingRequest;
    Member member;
    Zone zone;
    Category category;
    List<Member> memberDummyList;
    List<Member> memberList;

    @BeforeEach
    void setup() {
        matchingRequest = MatchingRequest.builder()
                .zoneId(1L)
                .categoryId(1L)
                .dayType("MON|TUE")
                .timeType("AFTERNOON")
                .preferMemberCount(4)
                .build();
        member = MemberDummy.createTestMember();
        zone = ZoneDummy.createZone();
        category = CategoryDummy.createCategory();
        memberDummyList = MemberDummy.createLeaders();
        memberList = new ArrayList<>(Arrays.asList(memberDummyList.get(0), memberDummyList.get(1)));
    }




    @DisplayName("matching ???????????? ?????? ????????? ?????? ?????? ??????")
    @Test
    void matching_fail() throws Exception {
        //given
        List<MemberFcmIncludeNicknameDto> dtoList = memberList.stream()
                .map(member -> MemberFcmIncludeNicknameDto.of(member.getId(), member.getFcmToken(),member.getNickname())).collect(Collectors.toList());

        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of(member.getUsername()));
        when(memberQueryRepository.findMemberFcmDtoByUsername(any()))
                .thenReturn(Optional.of(MemberFcmIncludeNicknameDto.of(member.getId(),member.getFcmToken(),member.getNickname())));
        when(matchingQueryRepository.findMemberFcmDtoByMatchForm(matchingRequest)).thenReturn(dtoList);

        //when
        matchingService.matching(matchingRequest);

        //then
        assertAll(
                () -> verify(securityUtil,times(1)).getCurrentUsername(),
                () -> verify(memberQueryRepository,times(1)).findMemberFcmDtoByUsername(any()),
                () -> verify(matchingQueryRepository,times(1)).findMemberFcmDtoByMatchForm(any()),
                () -> verify(matchingRepository, times(1)).save(any())
        );

    }

    @DisplayName("???????????? ????????? ?????? ??????")
    @Test
    void matching_success() throws Exception {
        //given
        memberList.add(memberDummyList.get(2));
        Team team = Team.builder()
                .id(1L)
                .build();

        List<MemberFcmIncludeNicknameDto> dtoList = memberList.stream()
                .map(member -> MemberFcmIncludeNicknameDto.of(member.getId(), member.getFcmToken(),member.getNickname())).collect(Collectors.toList());

        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of(member.getUsername()));
        when(memberQueryRepository.findMemberFcmDtoByUsername(any()))
                .thenReturn(Optional.of(MemberFcmIncludeNicknameDto.of(member.getId(),member.getFcmToken(),member.getNickname())));
        when(matchingQueryRepository.findMemberFcmDtoByMatchForm(matchingRequest)).thenReturn(dtoList);
        when(teamRepository.save(any())).thenReturn(team);


        //when
        matchingService.matching(matchingRequest);

        //then
        assertAll(
                () -> verify(securityUtil,times(1)).getCurrentUsername(),
                () -> verify(memberQueryRepository,times(1)).findMemberFcmDtoByUsername(any()),
                () -> verify(matchingQueryRepository,times(1)).findMemberFcmDtoByMatchForm(any()),

                () -> verify(teamRepository,times(1)).save(any()),
                () -> verify(teamCategoryRepository,times(1)).save(any()),
                () -> verify(teamJdbcRepository,times(1)).insertMemberTeamList(any(),any()),
                () -> verify(matchingRepository,times(1)).deleteAllWithBulkById(any()),
                () -> verify(eventPublisher,times(1)).publishEvent(ArgumentMatchers.any(MatchingCompleteEvent.class))
        );
    }

    @DisplayName("?????? ?????? ??? ?????? ?????? ?????? ??????")
    @Test
    void member_not_found() throws Exception {
        //given
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.empty());

        //when then
        assertThrows(MemberException.class,
                () -> matchingService.matching(matchingRequest));
    }

    @DisplayName("?????? ??????")
    @Test
    void cancel_matching() throws Exception{
        //given
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of(member.getUsername()));
        when(memberRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));

        //when
        matchingService.cancel();

        //then
        verify(memberRepository,times(1)).findIdByUsername(any());
        verify(matchingRepository,times(1)).deleteMatchingByMemberId(any());
    }

    @DisplayName("?????? ?????? ?????? - ?????????")
    @Test
    void check_current_user_matching_status_matching() throws Exception{
        //given
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of(member.getUsername()));
        when(matchingQueryRepository.existsByUsername(any())).thenReturn(true);

        //when
        MatchingStatusResponse matchingStatusResponse = matchingService.status();

        //then
        assertEquals(Status.MATCHING,matchingStatusResponse.getStatus());
    }

    @DisplayName("?????? ?????? ?????? - ????????????")
    @Test
    void check_current_user_matching_status_not_matching() throws Exception{
        //given
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of(member.getUsername()));
        when(matchingQueryRepository.existsByUsername(any())).thenReturn(false);

        //when
        MatchingStatusResponse matchingStatusResponse = matchingService.status();

        //then
        assertEquals(Status.NOT_MATCHING,matchingStatusResponse.getStatus());
    }



}
