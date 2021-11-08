package com.gjgs.gjgs.modules.notice.dto;

import com.gjgs.gjgs.modules.notice.entity.Notice;
import com.gjgs.gjgs.modules.notice.enums.NoticeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NoticeFormTest {
    @DisplayName("noticeForm 만들기")
    @Test
    void create_noticeForm() throws Exception{
        //given
        NoticeForm noticeForm = NoticeForm.builder()
                .title("title")
                .text("text")
                .noticeType(NoticeType.ALL)
                .build();

        //when
        Notice notice = Notice.of(noticeForm);

        //then
        assertAll(
                () -> assertEquals(noticeForm.getTitle(),notice.getTitle()),
                () -> assertEquals(noticeForm.getText(),notice.getText()),
                () -> assertEquals(noticeForm.getNoticeType(),notice.getNoticeType())
        );
    }
}
