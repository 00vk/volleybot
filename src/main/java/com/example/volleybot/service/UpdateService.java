package com.example.volleybot.service;

import com.example.volleybot.botapi.BotState;
import com.example.volleybot.botapi.BotStateContext;
import com.example.volleybot.cache.UserDataCache;
import com.example.volleybot.entity.Player;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by vkondratiev on 14.09.2021
 * Description:
 */

@Service
public class UpdateService {

    private final BotStateContext stateContext;
    private final UserDataCache userDataCache;
    private final PlayerService playerService;
    private final LoggerService loggerService;

    public UpdateService(BotStateContext stateContext,
                         UserDataCache userDataCache,
                         PlayerService playerService,
                         LoggerService loggerService) {
        this.stateContext = stateContext;
        this.userDataCache = userDataCache;
        this.playerService = playerService;
        this.loggerService = loggerService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage outMessage = null;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            outMessage = handleIncomingMessage(message);
        }
        return outMessage;
    }

    private SendMessage handleIncomingMessage(Message inMessage) {
        long chatId = inMessage.getChatId();
        Player player = playerService.findPlayer(chatId);
        BotState botState;
        if (player == null) {
            botState = BotState.AUTH;
        } else {
            botState = BotState.DEFAULT;
        }
        userDataCache.setUserBotState(chatId, botState);
        loggerService.logAction("action.authSuccess", "" + chatId);
        return stateContext.handler(botState).handle(inMessage);
    }
}
