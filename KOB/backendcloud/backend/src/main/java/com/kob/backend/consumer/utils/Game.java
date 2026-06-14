package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {

    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;

    @Getter
    private final int[][] g;
    final private static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    @Getter
    private final Player playerA, playerB;

    private Integer nextStepA = null;
    private Integer nextStepB = null;

    private ReentrantLock lock = new ReentrantLock();

    private String status = "playing";
    private String loser = "";

    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";

    public Game(Integer rows, Integer cols, Integer inner_walls_count,
                Integer idA, Bot botA, Integer idB, Bot botB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];

        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if (botA != null) { botIdA = botA.getId(); botCodeA = botA.getContent(); }
        if (botB != null) { botIdB = botB.getId(); botCodeB = botB.getContent(); }

        playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, botIdB, botCodeB, 1, cols - 2, new ArrayList<>());
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try { this.nextStepA = nextStepA; } finally { lock.unlock(); }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try { this.nextStepB = nextStepB; } finally { lock.unlock(); }
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;
        for (int i = 0; i < 4; i++) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if (check_connectivity(x, y, tx, ty)) { g[sx][sy] = 0; return true; }
            }
        }
        g[sx][sy] = 0;
        return false;
    }

    private boolean draw() {
        for (int i = 0; i < this.rows; i++)
            for (int j = 0; j < this.cols; j++)
                g[i][j] = 0;
        for (int r = 0; r < this.rows; r++) { g[r][0] = g[r][this.cols - 1] = 1; }
        for (int c = 0; c < this.cols; c++) { g[0][c] = g[this.rows - 1][c] = 1; }
        Random random = new Random();
        for (int i = 0; i < this.inner_walls_count / 2; i++) {
            for (int j = 0; j < 1000; j++) {
                int r = random.nextInt(this.rows), c = random.nextInt(this.cols);
                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) continue;
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }
        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        for (int i = 0; i < 1000; i++) if (draw()) break;
    }

    private String getInput(Player player) {
        Player me, you;
        if (player.getId().equals(playerA.getId())) { me = playerA; you = playerB; }
        else { me = playerB; you = playerA; }
        return getMapString() + "#" + me.getSx() + "#" + me.getSy() + "#(" +
                me.getStepsString() + ")#" + you.getSx() + "#" + you.getSy() + "#(" +
                you.getStepsString() + ")";
    }

    private void sendBotCode(Player player) {
        if (player.getBotId().equals(-1)) return;

        String botCode = player.getBotCode();
        if (botCode != null && botCode.startsWith("DEEPSEEK")) {
            sendDeepSeekBot(player, botCode);
            return;
        }

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", botCode);
        data.add("input", getInput(player));
        WebSocketServer.restTemplate.postForEntity(addBotUrl, data, String.class);
    }

    // ==================== DeepSeek 集成 ====================

    private List<int[]> computeSnakeBody(int sx, int sy, String stepsStr) {
        List<int[]> body = new ArrayList<>();
        String steps = stepsStr.substring(1, stepsStr.length() - 1);
        int x = sx, y = sy;
        body.add(new int[]{x, y});
        for (int i = 0; i < steps.length(); i++) {
            int d = steps.charAt(i) - '0';
            x += dx[d]; y += dy[d];
            body.add(new int[]{x, y});
            int step = i + 1;
            boolean increase = (step <= 10) || (step % 3 == 1);
            if (!increase) body.remove(0);
        }
        return body;
    }

    private List<Integer> computeSafeDirs(String mapStr, int[] myHead,
                                          List<int[]> myBody, List<int[]> youBody) {
        List<Integer> safe = new ArrayList<>();
        for (int d = 0; d < 4; d++) {
            int nx = myHead[0] + dx[d], ny = myHead[1] + dy[d];
            if (nx < 0 || nx >= rows || ny < 0 || ny >= cols) continue;
            if (mapStr.charAt(nx * cols + ny) == '1') continue;
            boolean hit = false;
            for (int[] c : myBody) if (c[0] == nx && c[1] == ny) { hit = true; break; }
            if (hit) continue;
            for (int[] c : youBody) if (c[0] == nx && c[1] == ny) { hit = true; break; }
            if (hit) continue;
            safe.add(d);
        }
        return safe;
    }

    private void sendDeepSeekBot(Player player, String botCode) {
        String input = getInput(player);
        int playerId = player.getId().equals(playerA.getId()) ? 0 : 1;

        String[] parts = input.split("#");
        String mapStr = parts[0];
        int meSx = Integer.parseInt(parts[1]), meSy = Integer.parseInt(parts[2]);
        String mySteps = parts[3];
        int youSx = Integer.parseInt(parts[4]), youSy = Integer.parseInt(parts[5]);
        String youSteps = parts[6];
        List<int[]> myBody = computeSnakeBody(meSx, meSy, mySteps);
        List<int[]> youBody = computeSnakeBody(youSx, youSy, youSteps);
        int[] myHead = myBody.get(myBody.size() - 1); // 最后一个才是当前蛇头！
        List<Integer> safeDirs = computeSafeDirs(mapStr, myHead, myBody, youBody);

        // 用开阔度启发式选最优方向（不调API，性能稳定）
        int dir;
        if (safeDirs.isEmpty()) {
            dir = 0;
            System.out.println("[AI] 无安全方向, 默认0");
        } else if (safeDirs.size() == 1) {
            dir = safeDirs.get(0);
            System.out.println("[AI] 仅一个安全=" + dir);
        } else {
            // 计算每个安全方向前方开阔度，选最开阔的
            int bestDir = safeDirs.get(0);
            int bestOpen = -1;
            for (int d : safeDirs) {
                int open = 0;
                for (int s = 1; s <= 10; s++) {
                    int cx = myHead[0] + dx[d] * s, cy = myHead[1] + dy[d] * s;
                    if (cx < 0 || cx >= rows || cy < 0 || cy >= cols) break;
                    if (mapStr.charAt(cx * cols + cy) == '1') break;
                    boolean blocked = false;
                    for (int[] c : myBody) if (c[0] == cx && c[1] == cy) { blocked = true; break; }
                    if (blocked) break;
                    for (int[] c : youBody) if (c[0] == cx && c[1] == cy) { blocked = true; break; }
                    if (blocked) break;
                    open++;
                }
                if (open > bestOpen) { bestOpen = open; bestDir = d; }
            }
            dir = bestDir;
            System.out.println("[AI] 多选=" + safeDirs + " 最开阔=" + dir + "(前方" + bestOpen + "格)");
        }

        if (playerId == 0) setNextStepA(dir);
        else setNextStepB(dir);
    }

    // ==================== 游戏主循环 ====================

    private boolean nextStep() {
        try { Thread.sleep(200); } catch (InterruptedException e) { throw new RuntimeException(e); }

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
                } finally { lock.unlock(); }
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        return false;
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        if (g[cell.x][cell.y] == 1) return false;
        for (int i = 0; i < n - 1; i++)
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y) return false;
        for (int i = 0; i < n - 1; i++)
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y) return false;
        return true;
    }

    private void judge() {
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();
        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if (!validA || !validB) {
            status = "finished";
            if (!validA && !validB) loser = "all";
            else if (!validA) loser = "A";
            else loser = "B";
        }
    }

    private void sendAllMessage(String message) {
        if (WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if (WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private void sendMove() {
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        } finally { lock.unlock(); }
    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < g.length; i++)
            for (int j = 0; j < g[0].length; j++)
                res.append(g[i][j]);
        return res.toString();
    }

    private void updateUserRating(Player player, Integer rating) {
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    private void saveToDatabase() {
        Integer ratingA, ratingB = 0;
        if (playerB.getId() < -1) {
            ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
            if ("A".equals(loser)) ratingA -= 2;
            else if ("B".equals(loser)) ratingA += 5;
            updateUserRating(playerA, ratingA);
        } else {
            ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
            ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();
            if ("A".equals(loser)) { ratingA -= 2; ratingB += 5; }
            else if ("B".equals(loser)) { ratingA += 5; ratingB -= 2; }
            updateUserRating(playerA, ratingA);
            updateUserRating(playerB, ratingB);
        }
        Record record = new Record(null,
                playerA.getId(), playerA.getSx(), playerA.getSy(),
                playerB.getId(), playerB.getSx(), playerB.getSy(),
                playerA.getStepsString(), playerB.getStepsString(),
                getMapString(), loser, new Date());
        WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            if (nextStep()) {
                judge();
                if (status.equals("playing")) sendMove();
                else { sendResult(); break; }
            } else {
                status = "finished";
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null) loser = "all";
                    else if (nextStepA == null) loser = "A";
                    else loser = "B";
                } finally { lock.unlock(); }
                sendResult();
                break;
            }
        }
    }
}
