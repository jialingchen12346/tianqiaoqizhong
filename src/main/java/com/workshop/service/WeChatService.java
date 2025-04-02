package com.workshop.service;

public interface WeChatService {
    /**
     * 发送微信模板消息
     * @param openId 接收者openId
     * @param title 标题
     * @param content 内容
     * @param page 跳转页面
     * @return 是否成功
     */
    boolean sendTemplateMessage(String openId, String title, String content, String page);

    /**
     * 获取小程序全局接口调用凭据
     * @return access_token
     */
    String getAccessToken();
} 