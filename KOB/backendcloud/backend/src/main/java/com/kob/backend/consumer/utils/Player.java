package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 用来维护玩家信息
public class Player {
    private Integer id;
    private Integer botId; // -1表示亲自出马， 否则表示用AI
    private String botCode;
    // 玩家在棋盘上的位置起点坐标 sx行数 sy列数 同步玩家位置
    private Integer sx;
    private Integer sy;

    private boolean check_tail_increasing(int step) { // 检查蛇什么时候会变长
        if (step < 10) return true;
        return step % 3 == 1;
    }

    // 表示方向 每一步方向
    private List<Integer> steps;

    public List<Cell> getCells() { // 创建蛇的身体
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for (int d: steps) { // 遍历方向
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y)); // 添加蛇的身体
            if (!check_tail_increasing( ++ step)) {
                res.remove(0); // 蛇头前移，蛇尾去掉
            }
        }
        return res;
    }

    public String getStepsString() {
        StringBuilder res = new StringBuilder();
        for (int d: steps) {
            res.append(d); // 将方向拼接成字符串
        }
        return res.toString();
    }
}
