package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by vkondratiev on 19.09.2021
 * Description:
 */
@Component
public class MainHandler implements IUpdateHandler {

    private final PlayerCache playerCache;
    private final TimetableManager timetableManager;

    public MainHandler(PlayerCache playerCache,
                       TimetableManager timetableManager) {
        this.playerCache = playerCache;
        this.timetableManager = timetableManager;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage()) {
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        boolean isAdmin = playerCache.isPlayerAdmin(chatId);
        if (isAdmin && "/checkdates".equalsIgnoreCase(message.getText())) {
            timetableManager.manageDates();
        }
    }

    @Override
    public BotState state() {
        return BotState.MAIN;
    }
}
