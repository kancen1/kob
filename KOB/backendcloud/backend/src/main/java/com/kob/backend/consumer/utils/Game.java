package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import lombok.Getter;
import org.springframework.security.core.parameters.P;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

// 继承Thread变为多线程类
public class Game extends Thread {

    // 用来管理游戏流程
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;

    // 地图
    @Getter
    private final int[][] g;
    // 定义四个方向
    final private static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    @Getter
    private final Player playerA, playerB;
    // 获取两面玩家下一步操作

    private Integer nextStepA = null;
    private Integer nextStepB = null;

    private ReentrantLock lock = new ReentrantLock(); // 定义锁

    private String status = "playing"; // playing --> finished
    private String loser = ""; // all 平局 A: a输 B: b输

    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";

    public Game(Integer rows,
                Integer cols,
                Integer inner_walls_count,
                Integer idA,
                Bot botA,
                Integer idB,
                Bot botB
    ) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];

        Integer botIdA = -1, botIdB = -1; // 设置默认值
        String botCodeA = "", botCodeB = "";
        if (botA != null) {
            // 表示是一个AI
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if (botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }

        // 初始化玩家对象 传入id 初始坐标 默认A在左下角B在右上角
        playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, botIdB, botCodeB,1, cols - 2, new ArrayList<>());
    }

    public void setNextStepA(Integer nextStepA) {
        // 锁上防止读写问题
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }


    // 判断是否连通
    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty)
            return true;
        // 标记当前位置
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i], y = sy + dy[i];
            // 判断越界
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                // 是否能搜到终点
                if (check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }

        // 恢复现场
        g[sx][sy] = 0;
        return false;
    }

    private boolean draw() { // 画地图
        // 清空
        for (int i = 0; i < this.rows; i ++ ) {
            for (int j = 0; j < this.cols; j ++ ) {
                // 0表示空气 1表示墙
                g[i][j] = 0;
            }
        }

        // 给四周加上障碍物
        for (int r = 0; r < this.rows; r ++ ) {
            // 左右加墙
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for (int c = 0; c < this.cols; c ++ ) {
            // 上下加墙
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        // 定义类用来随机
        Random random = new Random();
        // 创建随机的障碍物
        for (int i = 0; i < this.inner_walls_count / 2; i ++ ) {
            // 要是重复了就继续随机 这里定1000次
            for (int j = 0; j < 1000; j ++ ) {
                // 返回0-this.rows-1之间的随机值
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                // 如果重复了就继续循环  中心对称
                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1)
                    continue;

                // 保留右上角和左下角蛇出现的位置
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
                    continue;

                // 对称放障碍物
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        for (int i = 0; i < 1000 ; i ++) {
            if (draw())
                break;
        }
    }

    private String getInput(Player player) { // 将当前局面信息编码成字符串
        // 将局面传入 地图（障碍物用0 1表示） #隔开 横坐标me.sx #隔开 #纵坐标me.sy # 我的操作 # 对手坐标横坐标you.sx # you.sy # 对手操作
        Player me, you;
        if (player.getId().equals(playerA.getId())) { // 为玩家A
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }

        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" + // 因为操作序列有可能是空的
                me.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";
    }

    private void sendBotCode(Player player) {
        // 判断要不要发送代码让他自动执行
        if (player.getBotId().equals(-1)) return; // -1表示人操作, 不需要执行代码
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));

        WebSocketServer.restTemplate.postForEntity(addBotUrl, data, String.class);
    }

    private boolean nextStep() { // 等待两玩家下一步操作
        try {
            Thread.sleep(200); // 等待200ms为了不让前端被覆盖操作
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendBotCode(playerA);
        sendBotCode(playerB);

        for (int i = 0; i < 50; i++) {
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        // 判断头下一步是否到墙
        if (g[cell.x][cell.y] == 1) return false;

        for (int i = 0; i < n - 1; i ++ ) {
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y) // 判断头下一步是否撞到自己
                return false;
        }

        for (int i = 0; i < n - 1; i ++ ) {
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y) // 判断头下一步是否撞到对方
                return false;
        }

        return true;
    }

    private void judge() { // 判断两面玩家操作是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB); // 判断A是否合法
        boolean validB = check_valid(cellsB, cellsA); // 判断B是否合法

        if (!validA || !validB) {
            status = "finished"; // 游戏结束

            if (!validA && !validB) {
                loser = "all";
            } else if (!validA) {
                loser = "A";
            } else {
                loser = "B";
            }
        }
    }

    private void sendAllMessage(String message) { // 向两玩家发送信息
        if (WebSocketServer.users.get(playerA.getId()) != null) {
            WebSocketServer.users.get(playerA.getId()).sendMessage(message); // 向玩家A发送信息
        }

        if (WebSocketServer.users.get(playerB.getId()) != null) {
            WebSocketServer.users.get(playerB.getId()).sendMessage(message); // 向玩家B发送信息
        }
    }

    private void sendMove() { // 向两玩家发送移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null; // 清空下一步操作
        } finally {
            lock.unlock();
        }
    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < g.length; i ++ ) {
            for (int j = 0; j < g[0].length; j ++ ) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void updateUserRating(Player player, Integer rating) {
        // 更新玩家天梯积分
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user); // 更新数据库信息
    }

    private void saveToDatabase() { // 存储对局
        Integer ratingA;
        Integer ratingB = 0;
        if (playerB.getId() < -1) {
            // 如果是人机则只存储A的
            ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
            if ("A".equals(loser)) {
                ratingA -= 2;
            } else if ("B".equals(loser)) {
                ratingA += 5;
            }
        } else {
            ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
            ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();
            if ("A".equals(loser)) {
                ratingA -= 2;
                ratingB += 5;
            } else if ("B".equals(loser)) {
                ratingA += 5;
                ratingB -= 2;
            }
        }

        if (playerB.getId() < -1) {
            updateUserRating(playerA, ratingA);
        } else {
            updateUserRating(playerA, ratingA);
            updateUserRating(playerB, ratingB);
        }
        
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult() { // 向两个Client广播结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase(); // 存储对局
        sendAllMessage(resp.toJSONString()); // 向两玩家发送结果 因为要传入字符串
    }

    @Override
    // 开启新线程时入口函数 当线程启动后，run() 方法将被执行
    public void run() {
        for (int i = 0; i < 1000; i ++ ) { // 最多循环1000次
            if (nextStep()) { // 等待两玩家下一步操作 (是否获取)
                judge(); // 判断两玩家操作是否合法

                if (status.equals("playing")) {
                    sendMove(); // 向两玩家发送移动信息
                } else {
                    sendResult(); // 向两玩家发送结果
                    break; // 游戏结束
                }
            } else {
                status = "finished";
                lock.lock(); // 加锁 确保读入数据正确
                try {
                    if (nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else { // 超时
                        if (nextStepA == null) {
                            loser = "A";
                        } else {
                            loser = "B";
                        }
                    }
                } finally {
                    lock.unlock(); // 解锁
                }
                sendResult();
                break; // 游戏结束
            }
        }
    }
}
