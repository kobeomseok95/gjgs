package com.gjgs.gjgs.modules.dummy;

import com.gjgs.gjgs.modules.notification.dto.NotificationCreateRequest;
import com.gjgs.gjgs.modules.notification.enums.TargetType;

import java.util.List;

public class NotificationDummy {
    public static NotificationCreateRequest createNotificationForm(String title, String message, TargetType targetType, List<Long> memberIdList){
        return NotificationCreateRequest.builder()
                .title(title)
                .message(message)
                .targetType(targetType)
                .memberIdList(memberIdList)
                .build();
    }
}
