package com.example.volleybot.cache;

import com.example.volleybot.botapi.BotState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
@Component
public class PlayerCache {

    private final Map<Long, BotState> playerBotStates = new HashMap<>();

    public void setUserBotState(long chatId, BotState botState) {
        this.playerBotStates.put(chatId, botState);
    }

    public BotState botState(long chatId) {
        playerBotStates.putIfAbsent(chatId, BotState.START);
        return playerBotStates.get(chatId);
    }
}
