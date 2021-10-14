package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.service.SendMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

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
                            SendMessageService messageService) {
        this.playerCache = playerCache;
        this.messageService = messageService;
    }

    @Override
    public boolean handle(Message message) {
        Long userId = message.getFrom().getId();
        if (!message.hasText()) {
            return true;
        }
        String playerName = message.getText();
        boolean isAdmin = admins.contains(userId);
        playerCache.addNewPlayer(userId, playerName, isAdmin);
        messageService.log(logText(userId, playerName));
        playerCache.setUserBotState(userId, BotState.MAIN);
        return false;
    }

    @PostConstruct
    private void fillAdminList() {
        this.admins.addAll(Arrays.stream(adminsValue.split(" "))
                                 .map(Long::parseLong)
                                 .collect(Collectors.toList()));
    }

    private String logText(Long id, String name) {
        return "Зарегистрировался новый пользователь - " + name + " (id" + id + ")";
    }

    @Override
    public BotState state() {
        return BotState.NAME;
    }
}
