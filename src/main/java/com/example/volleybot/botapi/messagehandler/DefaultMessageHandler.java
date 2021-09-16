package com.example.volleybot.botapi.messagehandler;

import com.example.volleybot.botapi.BotState;
import com.example.volleybot.cache.UserDataCache;
import com.example.volleybot.service.SendMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
@Component
public class DefaultMessageHandler implements IMessageHandler {

    private final UserDataCache userDataCache;
    private final SendMessageService sendMessageService;

    public DefaultMessageHandler(UserDataCache userDataCache, SendMessageService sendMessageService) {
        this.userDataCache = userDataCache;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void handle(Message inMessage) {
        userDataCache.setUserBotState(inMessage.getChatId(), BotState.DEFAULT);
        sendMessageService.sendMessage(inMessage.getChatId(), "reply.default").run();
    }

    @Override
    public BotState state() {
        return BotState.DEFAULT;
    }
}
