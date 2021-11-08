package com.gjgs.gjgs.modules.favorite.dto;

import com.gjgs.gjgs.modules.team.entity.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyTeamAndIsIncludeFavoriteLectureDtoResponseTest {

    @DisplayName("MyTeamAndIsIncludeFavoriteLectureDtoResponse 생성")
    @Test
    void create_myTeamAndIsIncludeFavoriteLectureDtoResponse() throws Exception {
        //given
        Team team = Team.builder()
                .id(1L)
                .teamName("test")
                .build();
        MyTeamAndIsIncludeFavoriteLectureDto dto =
                MyTeamAndIsIncludeFavoriteLectureDto.of(team,true);

        //when
        MyTeamAndIsIncludeFavoriteLectureDtoResponse of
                = MyTeamAndIsIncludeFavoriteLectureDtoResponse.of(Arrays.asList(dto));

        //then
        assertEquals(dto, of.getMyTeamAndIsIncludeFavoriteLectureDtoList().get(0));
    }
}
