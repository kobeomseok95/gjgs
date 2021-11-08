package com.gjgs.gjgs.modules.favorite.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureTeamDtoResponse {
    private List<LectureTeamDto> lectureTeamDtoList;

    public static LectureTeamDtoResponse of(List<LectureTeamDto> lectureTeamDtoList) {
        return LectureTeamDtoResponse.builder()
                .lectureTeamDtoList(lectureTeamDtoList)
                .build();
    }
}
