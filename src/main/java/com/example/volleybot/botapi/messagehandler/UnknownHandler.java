package com.example.volleybot.botapi.messagehandler;

import com.example.volleybot.botapi.BotState;
import com.example.volleybot.cache.PlayerCache;
import com.example.volleybot.service.SendMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
@Component
public class UnknownHandler implements IMessageHandler {

    private final PlayerCache playerCache;
    private final SendMessageService sendMessageService;

    public UnknownHandler(PlayerCache playerCache, SendMessageService sendMessageService) {
        this.playerCache = playerCache;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void handle(Message inMessage) {
        playerCache.setUserBotState(inMessage.getChatId(), BotState.DEFAULT);
        sendMessageService.sendMessage(inMessage.getChatId(), "reply.default");
    }

    @Override
    public BotState state() {
        return BotState.DEFAULT;
    }
}
