package com.gjgs.gjgs.modules.favorite.exception;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum LectureTeamErrorCodes implements ErrorBase {

    LECTURE_TEAM_NOT_EXIST(HttpStatus.BAD_REQUEST.value(),
            "LECTURE_TEAM-400",
            "존재하지 않는 찜 번호입니다.");

    private int status;
    private String code;
    private String message;

    LectureTeamErrorCodes(int status, String code, String message) {
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
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getName() {
        return name();
    }
}
