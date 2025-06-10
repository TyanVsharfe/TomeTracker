package com.tometracker.controller;

import com.tometracker.db.model.PriceNotification;
import com.tometracker.db.model.User;
import com.tometracker.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("")
    public List<PriceNotification> getUserNotifications(@AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(0, 10);
        return notificationService.getUserNotifications(user, pageable);
    }

    @DeleteMapping("/{id}")
    public void deleteUserNotification(@AuthenticationPrincipal User user, @PathVariable("id") Long notificationId) {
        notificationService.deleteNotification(notificationId, user);
    }

    @PostMapping("/read/{id}")
    public void markNotificationAsRead(@AuthenticationPrincipal User user, @PathVariable("id") Long notificationId) {
        notificationService.markNotificationAsRead(notificationId, user);
    }

    @PostMapping("read/all")
    public void markAllNotificationsAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllNotificationsAsRead(user);
    }
}
