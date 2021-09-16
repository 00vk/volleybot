package com.example.volleybot.botapi.messagehandler;

import com.example.volleybot.botapi.BotState;
import com.example.volleybot.cache.UserDataCache;
import com.example.volleybot.service.PlayerService;
import com.example.volleybot.service.SendMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
@Component
public class AuthMessageHandler implements IMessageHandler {

    private final UserDataCache userDataCache;
    private final SendMessageService sendMessageService;
    private final PlayerService playerService;

    public AuthMessageHandler(UserDataCache userDataCache, SendMessageService sendMessageService, PlayerService playerService) {
        this.userDataCache = userDataCache;
        this.sendMessageService = sendMessageService;
        this.playerService = playerService;
    }

    @Override
    public void handle(Message inMessage) {
        userDataCache.setUserBotState(inMessage.getChatId(), BotState.DEFAULT);
        sendMessageService.sendMessage(inMessage.getChatId(), "reply.auth").run();
    }

    @Override
    public BotState state() {
        return BotState.AUTH;
    }
}
