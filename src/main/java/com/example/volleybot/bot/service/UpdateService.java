package com.example.volleybot.bot.service;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.BotStateContext;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.messagehandler.IUpdateHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Created by vkondratiev on 14.09.2021
 * Description:
 */

@Service
public class UpdateService {

    private final BotStateContext stateContext;
    private final PlayerCache playerCache;
    private final SendMessageService messageService;

    public UpdateService(BotStateContext stateContext,
                         PlayerCache playerCache,
                         SendMessageService messageService) {
        this.stateContext = stateContext;
        this.playerCache = playerCache;
        this.messageService = messageService;
    }

    public void onUpdateReceived(Update update) {
        User user = getUser(update);
        if (user == null || user.getIsBot()) return;

        logUpdate(update);
        changeBotState(update, user);
        handleUpdate(update, user);
    }

    private void handleUpdate(Update update, User user) {
        IUpdateHandler handler;
        try {
            do {
                BotState botState = playerCache.botState(user.getId());
                handler = stateContext.handler(botState);
            } while (!handler.handle(update));
        } catch (Exception e) {
            messageService.log(e.getMessage());
            handler = stateContext.handler(BotState.MAIN);
            handler.handle(update);
        }
    }

    private void changeBotState(Update update, User user) {
        if (!update.hasCallbackQuery()) return;
        CallbackQuery callback = update.getCallbackQuery();
        String data = callback.getData();
        playerCache.setUserBotState(user.getId(), BotState.of(data));
    }

    private User getUser(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (!message.hasText() || !message.getChat().isUserChat())
                return null;
            return message.getFrom();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom();
        }
        return null;
    }

    private void logUpdate(Update update) {
        User author;
        String text;

        if (update.hasMessage()) {
            Message message = update.getMessage();
            author = message.getFrom();
            text = message.getText();
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            author = callbackQuery.getFrom();
            text = callbackQuery.getData();
        } else {
            return;
        }

        String name = playerCache.getPlayerName(author.getId());
        if (name == null)
            name = author.getFirstName() + " " + author.getLastName();
        String logText = logText(name, text);
        messageService.log(logText);
    }

    private String logText(String name, String text) {
        return "*** " + name + ": " + text + " ***";
    }
}
