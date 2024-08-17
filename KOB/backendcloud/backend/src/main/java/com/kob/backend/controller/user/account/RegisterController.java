package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RegisterController {

    @Autowired
    RegisterService registerService;

    @PostMapping("/user/account/register/")
    // 获取前端参数
    public Map<String, String> register(@RequestParam Map<String, String> map) {
        // map是因为前端的ajax传给后端的是map 名字要和前端对应
        String username = map.get("username");
        String password = map.get("password");
        String confirmedPassword = map.get("confirmedPassword");
        return registerService.register(username, password, confirmedPassword);
    }
}
