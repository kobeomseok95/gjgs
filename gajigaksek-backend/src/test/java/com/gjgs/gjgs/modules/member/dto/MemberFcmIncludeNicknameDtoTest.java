package com.gjgs.gjgs.modules.member.dto;


import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.notification.dto.MemberFcmIncludeNicknameDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MemberFcmIncludeNicknameDtoTest {


    static MemberFcmIncludeNicknameDto dto;

    @BeforeAll
    static void setup(){
        dto = MemberFcmIncludeNicknameDto.of(1L, "fcm", "nick");
    }

    @DisplayName("생성")
    @Test
    void create_MemberFcmIncludeNicknameDto() throws Exception{
        //given


        //when then
        assertAll(
                () -> assertEquals(1L,dto.getMemberId()),
                () -> assertEquals("fcm",dto.getFcmToken()),
                () -> assertEquals("nick",dto.getNickname())
        );
    }

    @DisplayName("엔티티 생성")
    @Test
    void toEntity() throws Exception{

        //when
        Member member = dto.toEntity();

        //then
        assertAll(
                () -> assertEquals(dto.getMemberId(),member.getId()),
                () -> assertEquals(dto.getNickname(),member.getNickname()),
                () -> assertEquals(dto.getFcmToken(),member.getFcmToken())
        );
    }
}
