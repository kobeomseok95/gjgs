package com.gjgs.gjgs.modules.team.aop;

import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.exception.MemberErrorCodes;
import com.gjgs.gjgs.modules.member.exception.MemberException;
import com.gjgs.gjgs.modules.team.entity.Team;
import com.gjgs.gjgs.modules.team.entity.TeamApplier;
import com.gjgs.gjgs.modules.team.exceptions.TeamErrorCodes;
import com.gjgs.gjgs.modules.team.exceptions.TeamException;
import com.gjgs.gjgs.modules.team.repositories.interfaces.TeamQueryRepository;
import com.gjgs.gjgs.modules.utils.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class CheckLeaderAspect {

    private final SecurityUtil securityUtil;
    private final TeamQueryRepository teamQueryRepository;

    @AfterReturning(value = "@annotation(CheckLeader)", returning = "team")
    public void checkTeamLeader(Team team) {
        team.checkNotLeader(team.getLeader(), Member.ofUsername(getLeaderUsername()));
    }

    @AfterReturning(value = "@annotation(CheckLeader)", returning = "applierList")
    public void checkTeamLeaderForApplierList(List<TeamApplier> applierList) {
        Team team = applierList.get(0).getAppliedTeam();
        team.checkNotLeader(team.getLeader(), Member.ofUsername(getLeaderUsername()));
    }

    @Before(value = "@annotation(CheckLeaderBefore)")
    public void checkTeamLeaderBefore(JoinPoint joinPoint) {
        Long teamId = (Long) joinPoint.getArgs()[0];
        if (!teamQueryRepository.existByIdLeaderUsername(teamId, getLeaderUsername())) {
            throw new TeamException(TeamErrorCodes.TEAM_NOT_FOUND_OR_NOT_LEADER);
        }
    }

    private String getLeaderUsername() {
        return securityUtil.getCurrentUsername().orElseThrow(() -> new MemberException(MemberErrorCodes.MEMBER_NOT_FOUND));
    }
}
