package com.algovista.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.algovista.auth.dto.AuthRequest;
import com.algovista.auth.dto.UserProfile;
import com.algovista.common.exception.BusinessException;
import com.algovista.user.entity.User;
import com.algovista.user.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserProfile register(AuthRequest request) {
        Long exists = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, request.username()));
        if (exists > 0) {
            throw new BusinessException(409, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setNickname(StringUtils.hasText(request.nickname()) ? request.nickname() : request.username());
        userMapper.insert(user);
        return toProfile(user);
    }

    public UserProfile login(AuthRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, request.username()));
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        return toProfile(user);
    }

    public UserProfile findProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "登录状态已失效");
        }
        return toProfile(user);
    }

    private UserProfile toProfile(User user) {
        return new UserProfile(user.getId(), user.getUsername(), user.getNickname(), user.getAvatarUrl());
    }
}
