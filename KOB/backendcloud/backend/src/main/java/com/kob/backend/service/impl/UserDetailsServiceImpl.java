package com.kob.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//实现service.impl.UserDetailsServiceImpl类，继承自UserDetailsService接口，用来接入数据库信息
//实现config.SecurityConfig类，用来实现用户密码的加密存储

// 为了实现用户才可以使用页面
// 需要实现接口
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    // 需要实现接口方法

    // 使用数据库需要加上
    @Autowired
    private UserMapper userMapper;

    @Override // 重写方法
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            // 如果为空则报异常
            throw new RuntimeException("用户不存在");
        }

        return new UserDetailsImpl(user);
    }
}
