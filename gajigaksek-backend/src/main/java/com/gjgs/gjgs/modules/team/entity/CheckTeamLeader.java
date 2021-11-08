package com.gjgs.gjgs.modules.team.entity;

import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.team.exceptions.ExitMemberIsTeamLeaderException;
import com.gjgs.gjgs.modules.team.exceptions.TeamErrorCodes;
import com.gjgs.gjgs.modules.team.exceptions.TeamException;

public interface CheckTeamLeader {

    default void isLeaderDoThrow(Member leader, Member target) {
        if (leader.getId().equals(target.getId())) {
            throw new ExitMemberIsTeamLeaderException(TeamErrorCodes.EXIT_MEMBER_IS_LEADER);
        }
    }

    default void checkNotLeader(Member leader, Member target) {
        if (!isLeader(leader, target)) {
            throw new TeamException(TeamErrorCodes.NOT_TEAM_LEADER);
        }
    }

    private boolean isLeader(Member leader, Member target) {
        if (target.getId() != null) {
            return leader.getId().equals(target.getId());
        }
        return leader.getUsername().equals(target.getUsername());
    }
}
