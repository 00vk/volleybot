package com.example.volleybot.service;

import com.example.volleybot.botapi.BotState;
import com.example.volleybot.botapi.BotStateContext;
import com.example.volleybot.botapi.messagehandler.IMessageHandler;
import com.example.volleybot.cache.PlayerCache;
import com.example.volleybot.entity.Player;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by vkondratiev on 14.09.2021
 * Description:
 */

@Service
public class UpdateService {

    private final BotStateContext stateContext;
    private final PlayerCache playerCache;
    private final PlayerService playerService;
    private final SendMessageService sendMessageService;
    private String token;

    public UpdateService(BotStateContext stateContext,
                         PlayerCache playerCache,
                         PlayerService playerService,
                         SendMessageService sendMessageService) {
        this.stateContext = stateContext;
        this.playerCache = playerCache;
        this.playerService = playerService;
        this.sendMessageService = sendMessageService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            handleIncomingMessage(message);
        }
        return null;
    }

    private void handleIncomingMessage(Message inMessage) {
        long chatId = inMessage.getChatId();
        BotState botState = playerCache.botState(chatId);
        IMessageHandler handler = stateContext.handler(botState);
        handler.handle(inMessage);
    }

    public void setToken(String token) {
        this.token = token;
        sendMessageService.setToken(token);
    }
}
