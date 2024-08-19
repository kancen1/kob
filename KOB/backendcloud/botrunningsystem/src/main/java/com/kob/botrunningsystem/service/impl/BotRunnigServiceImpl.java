package com.kob.botrunningsystem.service.impl;

import com.kob.botrunningsystem.service.BotRunnigService;
import com.kob.botrunningsystem.service.impl.utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRunnigServiceImpl implements BotRunnigService {
    public static final BotPool botPoll = new BotPool();

    @Override
    public String addBot(Integer userId, String botCode, String input) {
        botPoll.addBot(userId, botCode, input);
        return "add bot success";
    }
}
