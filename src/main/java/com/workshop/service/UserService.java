package com.workshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workshop.model.User;
import java.util.List;

public interface UserService extends IService<User> {
    /**
     * 根据角色获取用户列表
     * @param role 角色
     * @return 用户列表
     */
    List<User> getUsersByRole(String role);
} 