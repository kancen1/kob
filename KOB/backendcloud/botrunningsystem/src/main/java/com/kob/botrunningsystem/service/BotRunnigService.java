package com.kob.botrunningsystem.service;

public interface BotRunnigService {
    // 输入参数 bot属于谁 bot代码 地图信息
    String addBot(Integer userId, String botCode, String input);
}
