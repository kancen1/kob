package com.kob.matchingsystem.controller;

import com.kob.matchingsystem.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class MatcingController {
    @Autowired
    private MatchingService matchingService;

    @PostMapping("/player/add/")
    public String addPlayer(@RequestParam MultiValueMap<String, String> data) { // MultiValueMap 一个键可以存储多个值
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id"))); // 获取user_id参数
        Integer rating = Integer.parseInt(Objects.requireNonNull(data.getFirst("rating"))); // 获取rating参数
        Integer botId = Integer.parseInt(Objects.requireNonNull(data.getFirst("bot_id"))); // 获得bot_id
        return matchingService.addPlayer(userId, rating, botId);
    }

    @PostMapping("/player/remove/")
    public String removePlayer(@RequestParam MultiValueMap<String, String> data) { // MultiValueMap 一个键可以存储多个值
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id"))); // 获取user_id参数
        return matchingService.removePlayer(userId);
    }
}
