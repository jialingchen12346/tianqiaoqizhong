package com.workshop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workshop.common.Result;
import com.workshop.model.Notification;
import com.workshop.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/unread/{userId}")
    public Result<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return Result.success(notifications);
    }

    @GetMapping("/page")
    public Result<Page<Notification>> getUserNotifications(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean isRead) {
        Page<Notification> page = new Page<>(current, size);
        Page<Notification> notificationPage = notificationService.getUserNotifications(page, userId, type, isRead);
        return Result.success(notificationPage);
    }

    @PutMapping("/read/{notificationId}")
    public Result<Boolean> markAsRead(@PathVariable Long notificationId) {
        boolean success = notificationService.markAsRead(notificationId);
        return Result.success(success);
    }

    @PutMapping("/readAll/{userId}")
    public Result<Boolean> markAllAsRead(@PathVariable Long userId) {
        boolean success = notificationService.markAllAsRead(userId);
        return Result.success(success);
    }
} 