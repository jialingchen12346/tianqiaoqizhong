package com.workshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.workshop.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
} 