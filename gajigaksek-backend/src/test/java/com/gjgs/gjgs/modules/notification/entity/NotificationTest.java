package com.gjgs.gjgs.modules.notification.entity;

import com.gjgs.gjgs.modules.dummy.MemberDummy;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.notification.dto.NotificationIncludeFcmToken;
import com.gjgs.gjgs.modules.notification.enums.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @DisplayName("notificationIncludeFcmToken으로 Notification 만들기")
    @Test
    void create_notification() throws Exception{
        //given
        Member member = MemberDummy.createTestMember();
        String title ="title";
        String message = "message";
        NotificationType notificationType = NotificationType.MATCHING_COMPLETE;
        String fcmToken ="fcmToken";

        NotificationIncludeFcmToken dto = NotificationIncludeFcmToken.of(member, title, message, notificationType, fcmToken);

        //when
        List<Notification> notification = Notification.of(List.of(dto));

        //then
        assertAll(
                () -> assertEquals(member,notification.get(0).getMember()),
                () -> assertEquals(title,notification.get(0).getTitle()),
                () -> assertEquals(message,notification.get(0).getMessage()),
                () -> assertEquals(false,notification.get(0).isChecked()),
                () -> assertEquals(notificationType,notification.get(0).getNotificationType()),
                () -> assertNotNull(notification.get(0).getUuid()),
                () -> assertNull(notification.get(0).getTeamId())
        );
    }

    @DisplayName("teamId 있는 Notification 만들기")
    @Test
    void create_notification_include_teamId() throws Exception{
        //given
        Member member = MemberDummy.createTestMember();
        String title ="title";
        String message = "message";
        NotificationType notificationType = NotificationType.MATCHING_COMPLETE;
        Long teamId = 1L;

        //when
        Notification notification = Notification.of(member, title, message, notificationType,teamId);

        //then
        assertAll(
                () -> assertEquals(member,notification.getMember()),
                () -> assertEquals(title,notification.getTitle()),
                () -> assertEquals(message,notification.getMessage()),
                () -> assertEquals(false,notification.isChecked()),
                () -> assertEquals(notificationType,notification.getNotificationType()),
                () -> assertEquals(teamId,notification.getTeamId()),
                () -> assertNotNull(notification.getUuid())

        );
    }
}


