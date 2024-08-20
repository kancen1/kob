package com.kob.backend.consumer;

// 使用WebSocketServer协议不同于http协议只能由前端发送后端后端才能回复
// WebSocketServer可以做到由后端访问前端
import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

// 标记类可以被 Spring 容器管理为 Bean
// 这意味着 Spring IoC 容器会自动检测到此类并将其实例化
@Component
// 指定了一个 WebSocket 服务器端点的 URL
// 在这个例子中，端点的路径是 "/websocket"，并且接受一个名为 "token" 的路径参数
// 客户端可以通过这个路径建立 WebSocket 连接
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    // 存放静态变量用户链接 创建线程安全的map
    public static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();

    // 存放用户信息
    private User user;

    // 后端向前端发送信息 导入API
    private Session session = null;

    // 因为不是标准Spring里面的不能直接用@Autowired
    public static UserMapper userMapper;
    public static RecordMapper recordMapper;
    public static BotMapper botMapper;

    public static RestTemplate restTemplate;  // 使两个进程之间通信

    public Game game = null;

    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }
    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        WebSocketServer.botMapper = botMapper;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }

    // 当一个新的 WebSocket 连接被打开时，此方法会被调用
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // session: 表示当前 WebSocket 连接的会话
        // token: 获取路径参数 "token" 的值
        // 当客户端尝试连接时，它必须提供一个有效的 "token" 参数
        // 建立连接 自动触发

        // 存放session
        this.session = session;
        System.out.println("connected!");

        // 利用工具类获得id WebSocket 认证不适用之前的 UsernamePasswordAuthenticationToken 和 Spring Security 的 SecurityContextHolder
        // 要使用自定义工具类解析令牌
        // JwtAuthentication 是一个工具类，用于解析 JWT 令牌并获取其中的用户信息
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);

        // 用户存在的话执行
        if (this.user != null) {
            users.put(userId, this);
        } else {
            // 否则断开连接
            this.session.close();
        }

        System.out.println(users);

    }

    // 当 WebSocket 连接关闭时，此方法会被调用
    // 这里没有参数传递进来，但你可以通过 @CloseReason 注解来获取关闭的原因
    @OnClose
    public void onClose() {
        // 关闭链接 自动触发
        System.out.println("disconnected!");

        // 删除这个链接
        if (this.user != null) {
            users.remove(this.user.getId());
        }

    }

    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId) {
        // 开始游戏
        User a = userMapper.selectById(aId), b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId), botB = botMapper.selectById(bBotId);

        Game game = new Game(
                13,
                14,
                20,
                a.getId(),
                botA,
                b.getId(),
                botB
        );
        game.createMap();

        if (users.get(a.getId()) != null) {
            users.get(a.getId()).game = game;
        }
        if (users.get(b.getId()) != null) {
            users.get(b.getId()).game = game;
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 另起一个线程 之前要sleep几秒钟等待前端不然会报错
        game.start();


        // 封装游戏信息到JSON中
        JSONObject respGame = new JSONObject();

        // 传入a信息
        respGame.put("a_id", game.getPlayerA().getId());
        // 传入a横坐标
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());

        // 传入b信息
        respGame.put("b_id", game.getPlayerB().getId());
        // 传入b横坐标
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        // 传入地图信息
        respGame.put("map", game.getG());


        // 传回给a和b配对成功
        JSONObject respA = new JSONObject();
        // 添加A的信息
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        // 添加游戏信息
        respA.put("game", respGame);

        // 传回前端A的信息 （获得A的链接向前端传递信息)  只有toJSONString后前端才需要解析 JSON对象不需要再次解析
        if (users.get(a.getId()) != null) {
            users.get(a.getId()).sendMessage(respA.toJSONString());
        }

        JSONObject respB = new JSONObject();
        // 添加B的信息
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        // 添加游戏信息
        respB.put("game", respGame);

        // 传回前端B的信息 （获得B的链接向前端传递信息)
        if (users.get(b.getId()) != null) {
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    private void startMatching(Integer botId) {
        System.out.println("start matching!");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        // 向匹配池添加玩家 参数为Url, data类型 , 返回类型的class
        data.add("bot_id", botId.toString());
        restTemplate.postForEntity(addPlayerUrl, data, String.class);
    }

    private void stopMatching() {
        System.out.println("stop matching!");
        // 取消匹配时从匹配池取出
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        // restTemplate可以向另一个后端发送请求
        restTemplate.postForEntity(removePlayerUrl, data, String.class);
    }

    private void move(int direction) {
        if (game.getPlayerA().getId().equals(user.getId())) {
            if (game.getPlayerA().getBotId().equals(-1)) // 人工操作
            // 如果A移动，则A的移动方向为传入的direction
              game.setNextStepA(direction);
        } else if (game.getPlayerB().getId().equals(user.getId())) { // 如果B移动，则B的移动方向为传入的direction
            if (game.getPlayerB().getBotId().equals(-1)) // 只有亲自出门才接受操作
                game.setNextStepB(direction);
        }
    }

    // 当从客户端(后端)收到一条消息时，此方法会被调用
    @OnMessage
    public void onMessage(String message, Session session) { // 当做一个路由
        // message: 接收的消息文本内容
        // session: 当前 WebSocket 会话对象，可以用来发送消息回客户端或执行其他与会话相关的操作
        // 从Client接收消息 后端接受到前端信息触发

        // 可以接受到前端所发来的请求
        // 解析前端发来的请求
        JSONObject data = JSONObject.parseObject(message);
        // 取出前端传入的event
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            // 如果等于开始匹配可以交给startMatching函数处理
            startMatching(data.getInteger("bot_id"));
        } else if ("stop-matching".equals(event)) {
            // 如果是stop matching交给该函数处理
            stopMatching();
        } else if ("move".equals(event)) {
            // 如果是move交给该函数处理
            move(data.getInteger("direction"));
        }
        System.out.println("receive message!");
    }

    // 当 WebSocket 会话中发生错误时，此方法会被调用
    @OnError
    public void onError(Session session, Throwable error) {
        // session: 发生错误的会话
        // error: 引发错误的异常对象
        // 通常在这里处理异常或记录日志
        error.printStackTrace();
    }

    // 实现从后端向前端发送信息
    public void sendMessage(String message) {
        // 异步通信过程需要加锁
        synchronized (this.session) {
            try {
                // 获取远程会话的基本远程对象
                this.session.getBasicRemote().sendText(message);
            }  catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}