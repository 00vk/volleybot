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
        Long fromId = message.getFrom().getId();
        String name = playerCache.getPlayerName(fromId);
        if (name == null) {
            User author = message.getFrom();
            name = author.getFirstName() + " " + author.getLastName();
        }
        if (message.hasText()) {
            String password = message.getText();
            if (PASS.equalsIgnoreCase(password)) {
                playerCache.setUserBotState(fromId, BotState.NAME);
                sendMessageService.sendMessage(fromId,  null, msgGetName());
                sendMessageService.log(logAuthSuccess(fromId, name));
            } else {
                sendMessageService.sendMessage(fromId, null, msgGetPass());
                sendMessageService.log(logAuthFailed(fromId, name));
            }
        }
    }

    private String msgGetPass() {
        return "Для продолжения требуется ввести пароль от эталона";
    }

    private String logAuthSuccess(Long id, String name) {
        return name + " (id" + id + ") успешно авторизовался";
    }

    private String logAuthFailed(Long id, String name) {
        return name + " (id" + id + ") ввел неверный пароль";
    }

    private String msgGetName() {
        return "Спасибо! Укажи свои имя и фамилию, чтобы я мог вносить тебя в расписание";
    }

    @Override
    public BotState state() {
        return BotState.AUTH;
    }
}