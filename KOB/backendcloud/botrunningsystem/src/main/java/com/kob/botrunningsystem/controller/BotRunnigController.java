package com.kob.botrunningsystem.controller;

import com.kob.botrunningsystem.service.BotRunnigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class BotRunnigController {
    @Autowired
    private BotRunnigService botRunnigService;

    @PostMapping("/bot/add/")
    private String addBot(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        String botCode = data.getFirst("bot_code");
        String input = data.getFirst("input");
        return botRunnigService.addBot(userId, botCode, input);
    }
}
