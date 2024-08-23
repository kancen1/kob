package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        System.out.println("receive bot move: " + userId + " " + direction + " ");
        if (userId < -1) {
            System.out.println("IA player A Id" + userId);
            if (WebSocketServer.users.get(-userId) != null) {
                Game game = WebSocketServer.users.get(-userId).game;
                if (game != null) {
                    System.out.println("B" + direction);
                    game.setNextStepB(direction);
                }
                return "receive botWithIA move success";
            }
        }
        if (WebSocketServer.users.get(userId) != null) {
            Game game = WebSocketServer.users.get(userId).game;

            if (game != null) {
                if (game.getPlayerA().getId().equals(userId)) {
                    // 如果A移动，则A的移动方向为传入的direction
                    game.setNextStepA(direction);
                } else if (game.getPlayerB().getId().equals(userId)) { // 如果B移动，则B的移动方向为传入的direction
                    game.setNextStepB(direction);
                }
            }
        }
        return "receive bot move success";
    }
}
