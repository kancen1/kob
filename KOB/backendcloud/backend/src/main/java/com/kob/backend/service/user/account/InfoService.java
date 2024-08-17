package com.kob.backend.service.user.account;

import java.util.Map;

// 根据token获得用户信息
public interface InfoService  {
    Map<String, String> getinfo();
}
