package com.workshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.workshop.model.Notification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
} 