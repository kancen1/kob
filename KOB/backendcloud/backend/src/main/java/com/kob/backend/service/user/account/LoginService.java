package com.kob.backend.service.user.account;

import java.util.Map;

public interface LoginService {
    // 习惯API会返回一个Map
    Map<String, String> getToken(String username, String password);

}
