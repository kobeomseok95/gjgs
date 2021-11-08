package com.gjgs.gjgs.modules.utils.s3.exception;

import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum S3ErrorCodes implements ErrorBase {

    UPLOAD_FAIL_NO_BUCKET(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "S3-101",
            "버킷이 존재하지 않아 파일 업로드에 실패했습니다."),
    UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "S3-102",
            "원인 미상으로 파일 업로드에 실패했습니다."),

    GET_URL_FAIL_NO_BUCKET(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "S3-103",
            "버킷이 존재하지 않아 파일 URL을 가져오는데 실패했습니다."),
    GET_URL_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "S3-104",
            "원인 미상으로 파일 URL을 가져오는데 실패했습니다."),

    DELETE_FAIL_NO_BUCKET(HttpStatus.BAD_REQUEST.value(),
            "S3-104",
            "버킷이 존재하지 않아 파일 삭제에 실패했습니다."),
    DELETE_FAIL(HttpStatus.BAD_REQUEST.value(),
            "S3-105",
            "원인 미상으로 파일 삭제에 실패했습니다."),

    FILE_CONVERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "S3-106",
            "MultipartFile -> File로 전환이 실패했습니다."),
    WRONG_FILE_FORMAT(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "S3-107",
            "잘못된 형식의 파일 입니다.");

    private int status;
    private String code;
    private String message;

    S3ErrorCodes(int status, String code, String message) {
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
