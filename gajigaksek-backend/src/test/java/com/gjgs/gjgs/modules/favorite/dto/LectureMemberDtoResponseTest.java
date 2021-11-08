package com.gjgs.gjgs.modules.favorite.dto;

import com.gjgs.gjgs.modules.lecture.embedded.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LectureMemberDtoResponseTest {

    @DisplayName("LectureMemberDtoResponse 생성")
    @Test
    void create_lectureMemberDtoResponse() throws Exception {
        //given
        LectureMemberDto lectureMemberDto = LectureMemberDto.builder()
                .lectureId(1L)
                .lectureMemberId(1L)
                .thumbnailImageFileUrl("test")
                .title("test")
                .zoneId(1L)
                .price(Price.builder()
                        .priceOne(50000)
                        .priceTwo(40000)
                        .priceThree(30000)
                        .priceFour(20000)
                        .build())
                .build();

        //when
        LectureMemberDtoResponse lectureMemberDtoResponse = LectureMemberDtoResponse.of(Arrays.asList(lectureMemberDto));

        //then
        assertEquals(lectureMemberDto, lectureMemberDtoResponse.getLectureMemberDtoList().get(0));
    }
}
