package com.workshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workshop.mapper.NotificationMapper;
import com.workshop.model.Notification;
import com.workshop.model.User;
import com.workshop.service.NotificationService;
import com.workshop.service.UserService;
import com.workshop.service.WeChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 通知服务实现类
 * 负责处理系统通知的创建、查询和状态更新
 * 包括任务通知、质检通知等，并集成微信消息推送功能
 */
@Slf4j
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Autowired
    private UserService userService;

    @Autowired
    private WeChatService weChatService;

    /**
     * 发送任务通知
     * @param userId 接收用户ID
     * @param type 通知类型
     * @param title 通知标题
     * @param content 通知内容
     * @param taskId 关联任务ID
     * @return 是否发送成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendTaskNotification(Long userId, String type, String title, String content, Long taskId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(taskId);
        notification.setIsRead(false);
        
        boolean success = this.save(notification);
        if (success) {
            // TODO: 调用微信消息推送接口
            sendWeChatNotification(userId, title, content);
        }
        return success;
    }

    /**
     * 发送质检通知
     * 向所有质检人员发送任务质检通知
     * @param taskName 任务名称
     * @param taskId 任务ID
     * @return 是否发送成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendQCNotification(String taskName, Long taskId) {
        // 查找所有质检人员
        List<User> qcUsers = userService.getUsersByRole("QC");
        for (User user : qcUsers) {
            Notification notification = new Notification();
            notification.setUserId(user.getId());
            notification.setType("QC_REQUIRED");
            notification.setTitle("待质检任务");
            notification.setContent("任务：" + taskName + " 需要质检");
            notification.setRelatedId(taskId);
            notification.setIsRead(false);
            this.save(notification);
            
            // TODO: 调用微信消息推送接口
            sendWeChatNotification(user.getId(), "待质检任务", "任务：" + taskName + " 需要质检");
        }
        return true;
    }

    /**
     * 获取用户未读通知列表
     * @param userId 用户ID
     * @return 未读通知列表，按创建时间降序排列
     */
    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getIsRead, false)
               .orderByDesc(Notification::getCreateTime);
        return this.list(wrapper);
    }

    /**
     * 分页查询用户通知
     * @param page 分页参数
     * @param userId 用户ID
     * @param type 通知类型(可选)
     * @param isRead 是否已读(可选)
     * @return 分页通知结果
     */
    @Override
    public Page<Notification> getUserNotifications(Page<Notification> page, Long userId, String type, Boolean isRead) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        
        if (type != null) {
            wrapper.eq(Notification::getType, type);
        }
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead);
        }
        
        wrapper.orderByDesc(Notification::getCreateTime);
        return this.page(page, wrapper);
    }

    /**
     * 标记单条通知为已读
     * @param notificationId 通知ID
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Long notificationId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getId, notificationId)
               .set(Notification::getIsRead, true);
        return this.update(wrapper);
    }

    /**
     * 标记用户所有未读通知为已读
     * @param userId 用户ID
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAllAsRead(Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getIsRead, false)
               .set(Notification::getIsRead, true);
        return this.update(wrapper);
    }

    /**
     * 发送微信通知
     * 这里需要集成微信消息推送接口
     */
    private void sendWeChatNotification(Long userId, String title, String content) {
        try {
            // 获取用户openId
            User user = userService.getById(userId);
            if (user != null && StringUtils.hasText(user.getOpenId())) {
                // 发送微信通知
                weChatService.sendTemplateMessage(
                    user.getOpenId(),
                    title,
                    content,
                    "/pages/task/task"  // 跳转到任务页面
                );
            }
        } catch (Exception e) {
            log.error("发送微信通知失败", e);
        }
    }
}
