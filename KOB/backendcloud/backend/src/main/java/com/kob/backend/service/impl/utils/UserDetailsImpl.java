package com.kob.backend.service.impl.utils;

import com.kob.backend.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data // get toSpring等自动填充好
@NoArgsConstructor // 无参数构造函数自动填充
@AllArgsConstructor // 有参自动填充
// 实现接口
public class UserDetailsImpl implements UserDetails {

    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        // 返回密码
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // 返回用户名
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 用户授权是否没有过期
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 是否没被锁定
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 用户证书是否没有过期
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 用户是否启用
        return true;
    }
}
