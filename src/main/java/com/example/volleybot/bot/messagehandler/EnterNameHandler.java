package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.service.SendMessageService;
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
    private final SendMessageService messageService;
    private final MainHandler mainHandler;
    private final List<Long> admins = new ArrayList<>();
    @Value("${telegrambot.admin-list}")
    private String adminsValue;

    public EnterNameHandler(PlayerCache playerCache,
                            SendMessageService messageService,
                            MainHandler mainHandler) {
        this.playerCache = playerCache;
        this.messageService = messageService;
        this.mainHandler = mainHandler;
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
            messageService.log(logText(chatId, playerName));
            mainHandler.handleAnyMessage(chatId, isAdmin);
        }
    }

    private String logText(Long chatId, String playerName) {
        return String.format("Зарегистрировался новый пользователь - %s (id%s)", playerName, chatId);
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
        return BotState.NAME;
    }
}
