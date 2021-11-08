package com.gjgs.gjgs.modules.favorite.dto;

import com.gjgs.gjgs.modules.bulletin.enums.Age;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FavoriteBulletinDtoResponseTest {

    @DisplayName("BulletinMemberDtoResponse 생성")
    @Test
    void create_bulletinMemberDtoResponse() throws Exception {
        //given
        FavoriteBulletinDto favoriteBulletinDto = FavoriteBulletinDto.builder()
                .bulletinId(1L)
                .bulletinMemberId(1L)
                .age(Age.THIRTY_TO_THIRTYFIVE)
                .currentPeople(3)
                .thumbnailImageFileUrl("image")
                .timeType("AFTERNOON")
                .title("test")
                .zoneId(1L)
                .build();
        //when
        FavoriteBulletinDtoResponse favoriteBulletinDtoResponse = FavoriteBulletinDtoResponse.of(Arrays.asList(favoriteBulletinDto));

        //then
        assertEquals(favoriteBulletinDto, favoriteBulletinDtoResponse.getFavoriteBulletinDtoList().get(0));
    }
}
