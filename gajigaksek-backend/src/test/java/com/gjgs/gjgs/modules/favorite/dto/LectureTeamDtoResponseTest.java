package com.gjgs.gjgs.modules.favorite.dto;

import com.gjgs.gjgs.modules.lecture.embedded.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LectureTeamDtoResponseTest {

    @DisplayName("LectureTeamDtoResponse 생성")
    @Test
    void create_lectureTeamDtoResponse() throws Exception {
        //given
        LectureTeamDto lectureTeamDto = LectureTeamDto.builder()
                .lectureId(1L)
                .lectureTeamId(1L)
                .thumbnailImageFileUrl("test")
                .zoneId(1L)
                .title("test")
                .price(Price.builder()
                        .priceOne(50000)
                        .priceTwo(40000)
                        .priceThree(30000)
                        .priceFour(20000)
                        .build())
                .build();

        //when
        LectureTeamDtoResponse lectureTeamDtoResponse = LectureTeamDtoResponse.of(Arrays.asList(lectureTeamDto));

        //then
        assertEquals(lectureTeamDto, lectureTeamDtoResponse.getLectureTeamDtoList().get(0));
    }
}
