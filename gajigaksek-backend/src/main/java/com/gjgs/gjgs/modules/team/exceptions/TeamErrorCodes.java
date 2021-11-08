package com.gjgs.gjgs.modules.team.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum TeamErrorCodes implements ErrorBase {

    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND.value(),
            "TEAM-404",
            "해당 팀이 존재하지 않습니다."),
    TEAM_CURRENT_PEOPLE_HIGHER(HttpStatus.BAD_REQUEST.value(),
            "TEAM-400",
            "팀의 현재 인원이 수정할 최대 인원 보다 많습니다."),
    TEAM_NOT_VALID(HttpStatus.BAD_REQUEST.value(),
            "TEAM-400",
            "입력 정보가 잘못되었습니다."),
    APPLIER_IN_TEAM(HttpStatus.CONFLICT.value(),
            "TEAM-409",
            "현재 신청자는 이 그룹에 속해있습니다."),
    TEAM_NOT_RECRUITMENT(HttpStatus.BAD_REQUEST.value(),
            "TEAM-400",
            "현재 팀원이 모두 차있거나, 모집하는 상태가 아닙니다."),
    NOT_TEAM_MEMBER(HttpStatus.NOT_FOUND.value(),
            "TEAM-404",
            "팀의 멤버가 아닙니다."),
    APPLIER_NOT_IN_TEAM_APPLIER_LIST(HttpStatus.NOT_FOUND.value(),
            "TEAM-404",
            "신청하지 않은 회원입니다."),
    NOT_TEAM_LEADER(HttpStatus.FORBIDDEN.value(),
            "TEAM-403",
            "팀장 권한이 없습니다."),
    EXIT_MEMBER_IS_LEADER(HttpStatus.BAD_REQUEST.value(),
            "TEAM-400",
            "팀장은 팀을 나갈 수 없습니다. 팀을 삭제할 수 있습니다."),
    TEAM_MEMBER_IS_MAX(HttpStatus.BAD_REQUEST.value(),
            "TEAM-400",
            "현재 팀 인원이 최대 인원입니다."),
    APPLIER_IN_APPLIER_LIST(HttpStatus.BAD_REQUEST.value(),
            "TEAM-400",
            "이미 가입신청한 팀입니다."),
    TEAM_NOT_FOUND_OR_NOT_LEADER(HttpStatus.FORBIDDEN.value(),
            "TEAM-403",
            "존재하지 않는 팀이거나 팀장 권한이 없는 팀입니다.");

    private int status;
    private String code;
    private String message;

    TeamErrorCodes(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public ErrorBase[] getValues() {
        return values();
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
