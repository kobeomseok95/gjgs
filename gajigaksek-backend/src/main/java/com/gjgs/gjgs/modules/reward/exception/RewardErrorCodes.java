package com.gjgs.gjgs.modules.reward.exception;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum RewardErrorCodes  implements ErrorBase {

    TYPE_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(),
            "REWARD-400",
            "REWARD의 타입이 올바르지 않습니다."),
    REWARD_NOT_ENOUGH(HttpStatus.BAD_REQUEST.value(),
            "REWARD-400",
            "현재 회원의 리워드 소유 금액이 부족합니다.")
    ;


    private int status;
    private String code;
    private String message;

    RewardErrorCodes(int status, String code, String message) {
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
