package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

// 指示Spring自动检测并注册一个Bean
@Component
public class MatchingPool extends Thread { // 多线程类
    private static List<Player> players = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    private static RestTemplate restTemplate;

    private final static String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating, Integer botId) {
        lock.lock();
        try {
            players.add(new Player(userId, rating, botId, 0));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            List<Player> newPlayers = new ArrayList<>();
            for (Player player : players) {
                if (!player.getUserId().equals(userId)) {
                    newPlayers.add(player); // 如果发现id不是我要删除的 将不等于userId的player添加到newPlayers中
                }
            }
            players = newPlayers; // 将players替换成newPlayers 完成删除
        } finally {
            lock.unlock();
        }
    }

    private void increaseWaitingTime() { // 将所有当前玩家的等待时间加一
        for (Player player : players) { // 遍历players
            player.setWaitingTime(player.getWaitingTime() + 1); // 每次调用increaseWaitingTime()方法，等待时间+1
        }
    }

    private boolean checkMatched(Player a, Player b) { // 判断两名玩家是否满足匹配要求
        int ratingDelta = Math.abs(a.getRating() - b.getRating()); // 计算两名玩家的rating差值
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime()); // 取两名玩家等待时间的最小值
        return ratingDelta <= waitingTime * 10; // 如果rating差值小于等于等待时间*10，则认为两名玩家匹配
    }

    private void sendResult(Player a, Player b) { // 返回a和b的匹配结果
        System.out.println("send result" + a + " " + b);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id", a.getUserId().toString());
        data.add("a_bot_id", a.getBotId().toString());
        data.add("b_id", b.getUserId().toString());
        data.add("b_bot_id", b.getBotId().toString());
        restTemplate.postForObject(startGameUrl, data, String.class);
    }

    private void mathPlayers() { // 尝试匹配所有玩家
        boolean[] used = new boolean[players.size()]; // 创建一个boolean数组，用于标记玩家是否已经匹配
        // 按照等待时间从长到短枚举玩家
        for (int i = 0; i < players.size(); i ++ ) { // 遍历players
            if (used[i]) continue; // 如果玩家已经匹配，跳过
            for (int j = i + 1; j < players.size(); j ++ ) {// 从i+1开始遍历players
                if (used[j]) continue; // 如果玩家已经匹配，跳过
                Player a = players.get(i), b = players.get(j); // 获取当前玩家a和b
                if (checkMatched(a, b)) { // 如果a和b匹配
                    used[i] = used[j] = true; // 将a和b标记为已匹配
                    sendResult(a, b); // 发送匹配结果
                    break; // 跳出循环
                }
            }
        }

        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i ++ ) { // 遍历players
            if (!used[i]) { // 如果玩家没有匹配
                newPlayers.add(players.get(i)); // 将玩家添加到newPlayers中
            }
        }

        players = newPlayers;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000); // 每隔一秒执行一次
                lock.lock();
                try {
                    increaseWaitingTime(); // 将所有当前玩家的等待时间加一
                    mathPlayers(); // 尝试匹配所有玩家
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
