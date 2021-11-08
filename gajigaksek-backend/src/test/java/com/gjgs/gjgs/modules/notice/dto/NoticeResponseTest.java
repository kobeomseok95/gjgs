package com.gjgs.gjgs.modules.notice.dto;

import com.gjgs.gjgs.modules.notice.entity.Notice;
import com.gjgs.gjgs.modules.notice.enums.NoticeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoticeResponseTest {

    @DisplayName("noticeDto 만들기")
    @Test
    void create_noticeDto() throws Exception{
        //given
        Notice notice = Notice.builder()
                .noticeType(NoticeType.ALL)
                .id(1L)
                .text("text")
                .title("title")
                .build();

        //when
        NoticeResponse noticeResponse = notice.toNoticeResponse();


        //then
        assertAll(
                () -> assertEquals(notice.getTitle(), noticeResponse.getTitle()),
                () -> assertEquals(notice.getText(), noticeResponse.getText()),
                () -> assertEquals(notice.getId(), noticeResponse.getNoticeId()),
                () -> assertNull(noticeResponse.getCreatedDate())
        );
    }
}
