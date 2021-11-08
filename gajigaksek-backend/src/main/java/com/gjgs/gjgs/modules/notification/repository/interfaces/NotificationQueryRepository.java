package com.gjgs.gjgs.modules.notification.repository.interfaces;

import com.gjgs.gjgs.modules.member.dto.mypage.NotificationResponse;
import com.gjgs.gjgs.modules.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface NotificationQueryRepository {
    Optional<Notification> findNotificationByUuidAndUsername(String uuid, String username);

    Slice<NotificationResponse> findNotificationByUsername(String username, Pageable pageable);
}
