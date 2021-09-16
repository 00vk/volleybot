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
public class UserDataCache {

    private final Map<Long, BotState> userBotStates = new HashMap<>();

    public void setUserBotState(long chatId, BotState botState) {
        this.userBotStates.put(chatId, botState);
    }

    public BotState getUserBotState(long chatId) {
        userBotStates.putIfAbsent(chatId, BotState.DEFAULT);
        return userBotStates.get(chatId);
    }
}
