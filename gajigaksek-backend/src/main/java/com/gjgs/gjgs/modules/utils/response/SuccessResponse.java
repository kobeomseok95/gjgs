package com.gjgs.gjgs.modules.utils.response;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SuccessResponse<T> {

    private static final String SUCCESS = "SUCCESS";

    private LocalDateTime time;

    private int status;

    private String message;

    private T data;

    public static SuccessResponse of() {
        return getSuccessResponse();
    }

    private static SuccessResponse getSuccessResponse() {
        return SuccessResponse.builder()
                .status(HttpStatus.OK.value())
                .message(SUCCESS)
                .time(LocalDateTime.now())
                .build();
    }

    public static <T> SuccessResponse of(T dto) {
        return getSuccessResponse(dto);
    }

    private static <T> SuccessResponse getSuccessResponse(T dto) {
        return SuccessResponse.builder()
                .status(HttpStatus.OK.value())
                .message(SUCCESS)
                .time(LocalDateTime.now())
                .data(dto)
                .build();
    }
}
