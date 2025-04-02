package com.workshop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.workshop.model.Notification;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    /**
     * 发送任务相关通知
     * @param userId 接收用户ID
     * @param type 通知类型
     * @param title 通知标题
     * @param content 通知内容
     * @param taskId 相关任务ID
     * @return 是否成功
     */
    boolean sendTaskNotification(Long userId, String type, String title, String content, Long taskId);

    /**
     * 发送质检通知
     * @param taskName 任务名称
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean sendQCNotification(String taskName, Long taskId);

    /**
     * 获取用户未读通知列表
     * @param userId 用户ID
     * @return 未读通知列表
     */
    List<Notification> getUnreadNotifications(Long userId);

    /**
     * 分页查询用户通知
     * @param page 分页参数
     * @param userId 用户ID
     * @param type 通知类型
     * @param isRead 是否已读
     * @return 通知分页数据
     */
    Page<Notification> getUserNotifications(Page<Notification> page, Long userId, String type, Boolean isRead);

    /**
     * 标记通知为已读
     * @param notificationId 通知ID
     * @return 是否成功
     */
    boolean markAsRead(Long notificationId);

    /**
     * 批量标记通知为已读
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAllAsRead(Long userId);
} 