package com.gjgs.gjgs.modules.notice.exception;


import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum NoticeErrorCodes implements ErrorBase {

    NOTICE_NOT_FOUND(HttpStatus.BAD_REQUEST.value(),
            "NOTICE-400",
                    "해당 id를 가진 공지사항이 없습니다."),
    NOTICE_TYPE_NOT_FOUND(HttpStatus.BAD_REQUEST.value(),
            "NOTICE-400",
            "존재하지 않는 Notice Type 입니다.")
    ;


    private int status;
    private String code;
    private String message;

    NoticeErrorCodes(int status, String code, String message) {
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
