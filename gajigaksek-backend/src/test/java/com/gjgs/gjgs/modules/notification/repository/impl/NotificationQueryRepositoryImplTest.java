package com.gjgs.gjgs.modules.notification.repository.impl;

import com.gjgs.gjgs.config.CustomDataJpaTest;
import com.gjgs.gjgs.modules.member.dto.mypage.NotificationResponse;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.notification.entity.Notification;
import com.gjgs.gjgs.modules.notification.enums.NotificationType;
import com.gjgs.gjgs.modules.notification.repository.interfaces.NotificationQueryRepository;
import com.gjgs.gjgs.modules.notification.repository.interfaces.NotificationRepository;
import com.gjgs.gjgs.testutils.repository.SetUpMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Import({
        NotificationQueryRepositoryImpl.class
})
@CustomDataJpaTest
class NotificationQueryRepositoryImplTest extends SetUpMemberRepository {

    @Autowired NotificationRepository notificationRepository;
    @Autowired NotificationQueryRepository notificationQueryRepository;

    @AfterEach
    void tearDown() throws Exception {
        notificationRepository.deleteAll();
    }

    @DisplayName("uuid와 username으로 알림 찾기")
    @Test
    void find_notification_by_uuid_and_username() throws Exception{
        //given
        Member member = anotherMembers.get(0);
        String title = "title";
        String message = "message";
        String uuid = UUID.randomUUID().toString();
        Notification notification = Notification.builder()
                .member(member)
                .title(title)
                .message(message)
                .checked(false)
                .notificationType(NotificationType.ADMIN_CUSTOM)
                .uuid(uuid)
                .build();
        notificationRepository.save(notification);
        flushAndClear();

        //when
        Notification noti = notificationQueryRepository.findNotificationByUuidAndUsername(uuid, member.getUsername()).get();
        //then
        assertAll(
                () -> assertEquals(title,noti.getTitle()),
                () -> assertEquals(message,noti.getMessage()),
                () -> assertEquals(member.getId(),noti.getMember().getId()),
                () -> assertEquals(notification.getNotificationType(),noti.getNotificationType()),
                () -> assertNotNull(noti.getCreatedDate()),
                () -> assertNotNull(noti.getLastModifiedDate()),
                () -> assertFalse(noti.isChecked())
        );
    }

    @DisplayName("해당 유저이름의 모든 알림 찾기")
    @Test
    void find_notification_by_username() throws Exception{

        //given
        Member member = anotherMembers.get(0);
        String title = "title";
        String message = "message";
        String uuid = UUID.randomUUID().toString();

        for(int i=0;i<15;i++){
            Notification notification = Notification.builder()
                    .member(member)
                    .title(title+i)
                    .message(message+i)
                    .checked(false)
                    .notificationType(NotificationType.ADMIN_CUSTOM)
                    .uuid(uuid)
                    .build();
            notificationRepository.save(notification);
        }

        Notification notification2 = Notification.builder()
                .member(director)
                .title(title)
                .message(message)
                .checked(false)
                .notificationType(NotificationType.ADMIN_CUSTOM)
                .uuid(uuid)
                .build();
        notificationRepository.save(notification2);

        flushAndClear();

        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC,"createdDate"));

        //when
        Slice<NotificationResponse> results = notificationQueryRepository.findNotificationByUsername(member.getUsername(), pageRequest);

        //then
        assertAll(
                () -> assertEquals(15,results.getContent().size()),
                () -> assertFalse(results.hasNext()),
                () -> assertNull(results.getContent().get(0).getTeamId()),
                () -> assertTrue(results.getContent().get(0).getTitle().contains(title)),
                () -> assertTrue(results.getContent().get(0).getMessage().contains(message)),
                () -> assertEquals(NotificationType.ADMIN_CUSTOM,results.getContent().get(0).getNotificationType()),
                () -> assertEquals(uuid,results.getContent().get(0).getUuid()),
                () -> assertNotNull(results.getContent().get(0).getCreatedDate())
        );
    }
}
