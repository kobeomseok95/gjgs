package com.gjgs.gjgs.modules.notification.controller;

import com.gjgs.gjgs.modules.notification.dto.NotificationCreateRequest;
import com.gjgs.gjgs.modules.notification.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 알림 보내기
     * @param notificationCreateRequest
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> sendNotification(@RequestBody @Valid NotificationCreateRequest notificationCreateRequest) {
        notificationService.sendCustomNotification(notificationCreateRequest);
        return ResponseEntity.ok().build();
    }


    /**
     * 알림 읽기
     * @param uuid
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{uuid}")
    public ResponseEntity<Void> readNotification(@PathVariable String uuid) {
        notificationService.readNotification(uuid);
        return ResponseEntity.ok().build();
    }

}
