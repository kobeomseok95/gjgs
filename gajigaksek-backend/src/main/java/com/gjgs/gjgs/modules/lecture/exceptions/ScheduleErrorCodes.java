package com.gjgs.gjgs.modules.lecture.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum ScheduleErrorCodes implements ErrorBase {

    SCHEDULE_NOT_FOUND(HttpStatus.BAD_REQUEST.value(),
            "SCHEDULE-404",
            "해당 스케줄이 존재하지 않습니다."),
    SCHEDULE_NOT_DELETE(HttpStatus.CONFLICT.value(),
            "SCHEDULE-409",
            "스케줄은 1명 이상일 경우 지울 수 없습니다."),
    SCHEDULE_IS_NOT_ABLE_TO_ENTER(HttpStatus.BAD_REQUEST.value(),
            "SCHEDULE-400",
            "해당 스케줄이 모집하는 인원을 초과합니다."),
    SCHEDULE_NOT_FOUND_OR_NOT_EXIST_PARTICIPANTS(HttpStatus.NOT_FOUND.value(),
            "SCHEDULE-404",
            "해당 스케줄이 존재하지 않거나 참여자가 없습니다."),
    SCHEDULE_NOT_EXIT(HttpStatus.CONFLICT.value(),
            "SCHEDULE-409",
            "모집 확정 상태이거나 클래스를 이미 진행했던 경우는 클래스 신청을 취소할 수 없습니다."),
    INVALID_APPLY_SCHEDULE(HttpStatus.NOT_FOUND.value(),
            "SCHEDULE-404",
            "해당 스케줄이 존재하지 않습니다."),
    PREVIOUS_ENTERED_PARTICIPANT(HttpStatus.CONFLICT.value(),
            "SCHEDULE-409",
            "참여 인원중에 이미 참여중인 회원이 있습니다."),
    SCHEDULE_NOT_RECRUIT(HttpStatus.BAD_REQUEST.value(),
            "SCHEDULE-400",
            "모집 중인 상태만 참여할 수 있습니다."),
    SCHEDULE_OVER_TIME(HttpStatus.BAD_REQUEST.value(),
            "SCHEDULE-400",
            "클래스 시작 시간이 지났습니다. 팀 신청 시, 클래스 시작 시간 한 시간 전에 신청 가능합니다."),
    ACTOR_SHOULD_NOT_DIRECTOR(HttpStatus.BAD_REQUEST.value(),
            "SCHEDULE-400",
                    "디렉터는 본인의 클래스를 신청할 수 없습니다."),
    SCHEDULE_OVER_PARTICIPANTS(HttpStatus.CONFLICT.value(),
            "SCHEDULE-409",
            "신청할 수 있는 참가 인원을 초과했습니다."),
    NOT_END_STATUS(HttpStatus.BAD_REQUEST.value(),
            "SCHEDULE-400",
            "리뷰를 작성할 수 있는 클래스 상태가 아닙니다."),
    NOT_PARTICIPANT(HttpStatus.BAD_REQUEST.value(),
            "SCHEDULE-400",
            "리뷰를 작성할 수 있는 참가자가 아닙니다."),
    SCHEDULE_NOT_CHANGE_END(HttpStatus.BAD_REQUEST.value(),
            "SCHEDULE-400",
            "현재 시간이 클래스 마감 시간을 지나지 않았습니다.")
    ;

    private int status;
    private String code;
    private String message;

    ScheduleErrorCodes(int status, String code, String message) {
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
