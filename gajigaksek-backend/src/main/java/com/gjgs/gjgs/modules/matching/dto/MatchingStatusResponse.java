package com.gjgs.gjgs.modules.matching.dto;

import com.gjgs.gjgs.modules.matching.enums.Status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MatchingStatusResponse {

    private Status status;

    public static MatchingStatusResponse of(Status status){
        return MatchingStatusResponse.builder()
                .status(status)
                .build();
    }

}
