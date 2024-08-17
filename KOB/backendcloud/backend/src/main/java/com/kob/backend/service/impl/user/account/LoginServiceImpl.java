package com.kob.backend.service.impl.user.account;

import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.account.LoginService;
import com.kob.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// 用来实现LoginService接口Impl
@Service // 实现Service加上注解
public class LoginServiceImpl implements LoginService {
    @Autowired  //验证用户是否登入成功的API
    private AuthenticationManager authenticationManager;

    @Override
    public Map<String, String> getToken(String username, String password) {
        // 封装用户名密码（加密）  因为数据库中不是明文   password就会存加密后的密码
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        // 验证是否正常登入  加密后对比加密后的
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);  // 如果登入失败会自动处理

        // 验证成功后就可以取出用户
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        User user = loginUser.getUser();

        // 将用户id封装成jwt token
        String jwt = JwtUtil.createJWT(user.getId().toString());

        Map<String, String> map = new HashMap<>();
        // 成功的话提示
        map.put("error_message", "success");
        map.put("token", jwt);

        return map;
    }
}
