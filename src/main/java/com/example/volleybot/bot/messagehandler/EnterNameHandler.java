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
    private final List<Long> admins = new ArrayList<>();
    @Value("${telegrambot.admin-list}")
    private String adminsValue;

    public EnterNameHandler(PlayerCache playerCache,
                            SendMessageService messageService,
                            MainHandler mainHandler) {
        this.playerCache = playerCache;
        this.messageService = messageService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage()) {
            return;
        }
        Message message = update.getMessage();
        Long fromId = message.getFrom().getId();
        if (message.hasText()) {
            String playerName = message.getText();
            boolean isAdmin = admins.contains(fromId);
            playerCache.setUserBotState(fromId, BotState.MAIN);
            playerCache.addNewPlayer(fromId, playerName, isAdmin);
            messageService.log(logText(fromId, playerName));
            sendMainMessage(fromId, isAdmin);
        }
    }

    private String logText(Long id, String name) {
        return "Зарегистрировался новый пользователь - " + name + " (id" + id + ")";
    }

    private void sendMainMessage(Long chatId, boolean isAdmin) {
        messageService.sendMessage(chatId, null, getMainMessage(isAdmin));
    }

    private String getMainMessage(boolean isAdmin) {
        return """
                Доступные команды:
                /visit - записаться на игру
                /settings - настройки
                """;
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
