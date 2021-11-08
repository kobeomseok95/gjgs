package com.gjgs.gjgs.modules.notification.exception;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum NotificationErrorCodes implements ErrorBase {


    NOT_EXIST_TYPE(HttpStatus.BAD_REQUEST.value(),
            "NOTIFICATION-400",
            "존재하지 않는 대상 타입입니다."
    ),
    NOT_EXIST_NOTIFICATION(HttpStatus.BAD_REQUEST.value(),
            "NOTIFICATION-400",
            "존재하지 않는 알림입니다."
    )
    ;

    private int status;
    private String code;
    private String message;

    NotificationErrorCodes(int status, String code, String message) {
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
