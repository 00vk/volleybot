package com.example.volleybot.bot.service;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.BotStateContext;
import com.example.volleybot.bot.messagehandler.IUpdateHandler;
import com.example.volleybot.bot.cache.PlayerCache;
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
    private final SendMessageService sendMessageService;

    public UpdateService(BotStateContext stateContext,
                         PlayerCache playerCache, SendMessageService sendMessageService) {
        this.stateContext = stateContext;
        this.playerCache = playerCache;
        this.sendMessageService = sendMessageService;
    }

    public void handleUpdate(Update update) {
        logUpdate(update);
        long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getFrom().getId();
        } else {
            return;
        }
        BotState botState = playerCache.botState(chatId);
        IUpdateHandler handler = stateContext.handler(botState);
        handler.handle(update);
    }

    private void logUpdate(Update update) {
        long chatId;
        String text;
        User author;
        Message message;

        if (update.hasMessage()) {
            message = update.getMessage();
            author = message.getFrom();
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            author = callbackQuery.getFrom();
            message = callbackQuery.getMessage();
        } else {
            return;
        }

        chatId = author.getId();
        text = message.getText();
        String name = playerCache.getPlayerName(chatId);
        if (name == null)
            name = author.getFirstName() + " " + author.getLastName();
        String logText = logText(name, text);
        sendMessageService.log(logText);
    }

    private String logText(String name, String text) {
        return String.format("*** %s: %s ***", name, text);
    }
}
