package com.example.volleybot.botapi.messagehandler;

import com.example.volleybot.botapi.BotState;
import com.example.volleybot.cache.PlayerCache;
import com.example.volleybot.service.SendMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Created by vkondratiev on 17.09.2021
 * Description:
 */
@Component
public class AuthHandler implements IMessageHandler {

    private final String PASS = "elsoft";
    private final PlayerCache playerCache;
    private final SendMessageService sendMessageService;

    public AuthHandler(PlayerCache playerCache, SendMessageService sendMessageService) {
        this.playerCache = playerCache;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void handle(Message inMessage) {
        Long chatId = inMessage.getChatId();
        playerCache.setUserBotState(chatId, BotState.AUTH);
        sendMessageService.sendMessage(chatId, "start");
        sendMessageService.log("action.start", "" + chatId);
    }

    @Override
    public BotState state() {
        return BotState.AUTH;
    }
}