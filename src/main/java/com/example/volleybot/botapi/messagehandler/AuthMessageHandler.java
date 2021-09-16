package com.example.volleybot.botapi.messagehandler;

import com.example.volleybot.botapi.BotState;
import com.example.volleybot.cache.UserDataCache;
import com.example.volleybot.service.PlayerService;
import com.example.volleybot.service.ReplyMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
@Component
public class AuthMessageHandler implements IMessageHandler {

    private final UserDataCache userDataCache;
    private final ReplyMessageService replyMessageService;
    private final PlayerService playerService;

    public AuthMessageHandler(UserDataCache userDataCache, ReplyMessageService replyMessageService, PlayerService playerService) {
        this.userDataCache = userDataCache;
        this.replyMessageService = replyMessageService;
        this.playerService = playerService;
    }

    @Override
    public SendMessage handle(Message inMessage) {
        userDataCache.setUserBotState(inMessage.getChatId(), BotState.DEFAULT);
        return replyMessageService.getSendMessage(inMessage.getChatId().toString(), "reply.auth");
    }

    @Override
    public BotState state() {
        return BotState.AUTH;
    }
}
