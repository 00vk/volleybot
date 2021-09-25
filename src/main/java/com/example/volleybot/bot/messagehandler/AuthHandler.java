package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.service.SendMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Created by vkondratiev on 17.09.2021
 * Description:
 */
@Component
public class AuthHandler implements IUpdateHandler {

    private static final String PASS = "elsoft";
    private final PlayerCache playerCache;
    private final SendMessageService sendMessageService;

    public AuthHandler(PlayerCache playerCache, SendMessageService sendMessageService) {
        this.playerCache = playerCache;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage()) {
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String name = playerCache.getPlayerName(chatId);
        if (name == null) {
            User author = message.getFrom();
            name = author.getFirstName() + " " + author.getLastName();
        }
        if (message.hasText()) {
            String password = message.getText();
            if (PASS.equalsIgnoreCase(password)) {
                playerCache.setUserBotState(chatId, BotState.NAME);
                sendMessageService.sendMessage(chatId,  null, msgGetName());
                sendMessageService.log(logAuthSuccess(chatId, name));
            } else {
                sendMessageService.sendMessage(chatId, null, msgGetPass());
                sendMessageService.log(logAuthFailed(chatId, name));
            }
        }
    }

    private String msgGetPass() {
        return "Для продолжения требуется ввести пароль от эталона";
    }

    private String logAuthSuccess(Long chatId, String username) {
        return username + " (id" + chatId + ") успешно авторизовался";
    }

    private String logAuthFailed(Long chatId, String username) {
        return username + " (id" + chatId + ") ввел неверный пароль";
    }

    private String msgGetName() {
        return "Спасибо! Укажи свои имя и фамилию, чтобы я мог вносить тебя в расписание";
    }

    @Override
    public BotState state() {
        return BotState.AUTH;
    }
}