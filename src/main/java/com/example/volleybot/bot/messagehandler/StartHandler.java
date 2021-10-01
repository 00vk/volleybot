package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.service.SendMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 * Бот запущен
 */
@Component
public class StartHandler implements IUpdateHandler {

    private final PlayerCache playerCache;
    private final SendMessageService sendMessageService;

    public StartHandler(PlayerCache playerCache, SendMessageService sendMessageService) {
        this.playerCache = playerCache;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage()) {
            return;
        }
        Message message = update.getMessage();
        User author = message.getFrom();
        Long authorId = author.getId();
        String telegramName = author.getFirstName() + " " + author.getLastName();
        playerCache.setUserBotState(authorId, BotState.AUTH);
        playerCache.updatePlayer(authorId, telegramName);
        sendMessageService.sendMessage(authorId, null, msgStart());
        sendMessageService.log(logText(authorId, telegramName));
    }

    private String msgStart() {
        return "Привет! Чтобы продолжить, требуется авторизоваться. Введи пароль от эталона.";
    }

    private String logText(Long id, String telegramName) {
        return telegramName + " (id" + id + ") запустил бота";
    }

    @Override
    public BotState state() {
        return BotState.START;
    }
}
