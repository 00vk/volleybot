package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.service.SendMessageService;
import com.example.volleybot.db.service.PlayerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vkondratiev on 19.09.2021
 * Description:
 */
@Component
public class EnterNameHandler implements IUpdateHandler {

    private final PlayerCache playerCache;
    private final SendMessageService sendMessageService;
    @Value("${telegrambot.admin-list}")
    private String adminsValue;
    private final List<Long> admins = new ArrayList<>();

    public EnterNameHandler(PlayerCache playerCache,
                            SendMessageService sendMessageService) {
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
        if (message.hasText()) {
            String playerName = message.getText();
            boolean isAdmin = admins.contains(chatId);
            playerCache.setUserBotState(chatId, BotState.MAIN);
            playerCache.addNewPlayer(chatId, playerName, isAdmin);
            String msgMain = getString(isAdmin);
            sendMessageService.sendMessage(chatId,null, msgMain);
            sendMessageService.log(logText(chatId, playerName));
        }
    }

    private String logText(Long chatId, String playerName) {
        return String.format("Зарегистрировался новый пользователь - %s (id%s)", playerName, chatId);
    }

    private String getString(boolean isAdmin) {
        return "Доступные команды:\n/go - записаться на игру\n/cancel - отменить запись\n/settings - настройки";
    }

    @PostConstruct
    private void fillAdminList() {
        if (!this.admins.isEmpty()) {
            return;
        }
        this.admins.addAll(Arrays.stream(adminsValue.split(" "))
                                 .map(Long::parseLong)
                                 .collect(Collectors.toList()));
    }

    @Override
    public BotState state() {
        return BotState.AUTH_SUCCESS;
    }
}
