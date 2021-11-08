package com.gjgs.gjgs.modules.lecture.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum LectureErrorCodes implements ErrorBase {

    LECTURE_NOT_FOUND(HttpStatus.BAD_REQUEST.value(),
            "LECTURE-404",
            "해당 클래스가 존재하지 않습니다."),
    THUMBNAIL_IS_NOT_ONE(HttpStatus.BAD_REQUEST.value(),
            "LECTURE-400",
            "대표 사진은 한장만 필요합니다."),
    PRODUCT_AND_FILE_NOT_EQUAL(HttpStatus.BAD_REQUEST.value(),
            "LECTURE-400",
            "사진의 갯수와 완성작 혹은 커리큘럼 정보의 갯수가 일치하지 않습니다."),
    FINISHED_PRODUCT_FILE_SIZE(HttpStatus.BAD_REQUEST.value(),
            "LECTURE-400",
            "완성작 사진 갯수는 1장 이상 4장 이하여야만 합니다."),
    CURRICULUM_FILE_NOT_EQUAL(HttpStatus.BAD_REQUEST.value(),
            "LECTURE-400",
                    "사진의 갯수와 등록할 커리큘럼의 갯수가 일치하지 않습니다."),
    TEMPORARY_NOT_SAVE_LECTURE(HttpStatus.NOT_FOUND.value(),
            "LECTURE-404",
            "디렉터가 임시 저장한 클래스 정보가 없습니다."),
    NOT_EXIST_CONDITION(HttpStatus.BAD_REQUEST.value(),
            "LECTURE-400",
            "내가 진행하는 클래스를 찾는 조건이 맞지 않습니다."),
    INVALID_TEMPORARY_STORE_STEP(HttpStatus.NOT_FOUND.value(),
            "LECTURE-404",
            "존재하지 않는 임시 저장된 클래스입니다."),
    DIRECTOR_HAVE_NOT_LECTURE(HttpStatus.NOT_FOUND.value(),
            "LECTURE-404",
            "디렉터가 만든 클래스가 없습니다."),
    INVALID_LECTURE(HttpStatus.FORBIDDEN.value(),
            "LECTURE-403",
            "내 클래스가 아니거나 검수가 완료된 클래스가 아닙니다."),
    INVALID_APPLY_TYPE(HttpStatus.BAD_REQUEST.value(),
            "LECTURE-400",
            "유효한 클래스 신청 방법이 아닙니다. 팀 신청과 개인 신청만 존재합니다."),
    INVALID_APPLY_LECTURE_SCHEDULE(HttpStatus.BAD_REQUEST.value(),
            "LECTURE-400",
            "모집중인 스케줄이 아니거나 종료된 클래스입니다."),
    INVALID_ACTION_CAUSE_I_AM_DIRECTOR(HttpStatus.BAD_REQUEST.value(),
            "LECTURE-400",
            "해당 기능은 클래스 개설자가 본인일 경우 이용할 수 없습니다."),
    INVALID_DECIDE_LECTURE_TYPE(HttpStatus.BAD_REQUEST.value(),
            "LECTURE-400",
            "유효한 클래스 검수 결정이 아닙니다."),
    NOT_REJECT_LECTURE(HttpStatus.BAD_REQUEST.value(),
            "LECTURE-400",
            "검수 거절된 클래스가 아닙니다."),
    EXIST_CREATING_LECTURE(HttpStatus.CONFLICT.value(),
            "LECTURE-409",
            "이미 작성중인 클래스가 있습니다. 작성중인 클래스 혹은 검수 거절된 클래스를 삭제해주세요.")
    ;

    private int status;
    private String code;
    private String message;

    LectureErrorCodes(int status, String code, String message) {
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
