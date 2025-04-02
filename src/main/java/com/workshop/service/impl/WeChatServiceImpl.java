package com.workshop.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.config.WeChatConfig;
import com.workshop.service.WeChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WeChatServiceImpl implements WeChatService {

    @Autowired
    private WeChatConfig weChatConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appId}&secret={appSecret}";
    private static final String SEND_TEMPLATE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={accessToken}";

    @Override
    public boolean sendTemplateMessage(String openId, String title, String content, String page) {
        try {
            String accessToken = getAccessToken();
            
            Map<String, Object> data = new HashMap<>();
            data.put("touser", openId);
            data.put("template_id", weChatConfig.getTemplateId());
            data.put("page", page);
            
            // 模板数据
            Map<String, Object> templateData = new HashMap<>();
            // thing1: 标题
            templateData.put("thing1", new HashMap<String, String>() {{
                put("value", title);
            }});
            // thing2: 内容
            templateData.put("thing2", new HashMap<String, String>() {{
                put("value", content);
            }});
            data.put("data", templateData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(data), headers);

            String response = restTemplate.postForObject(
                SEND_TEMPLATE_MESSAGE_URL,
                request,
                String.class,
                accessToken
            );

            log.info("发送微信消息结果: {}", response);
            return true;
        } catch (Exception e) {
            log.error("发送微信消息失败", e);
            return false;
        }
    }

    @Override
    public String getAccessToken() {
        try {
            String response = restTemplate.getForObject(
                ACCESS_TOKEN_URL,
                String.class,
                weChatConfig.getAppId(),
                weChatConfig.getAppSecret()
            );
            Map<String, Object> result = objectMapper.readValue(response, Map.class);
            return (String) result.get("access_token");
        } catch (Exception e) {
            log.error("获取access_token失败", e);
            throw new RuntimeException("获取access_token失败", e);
        }
    }
} 