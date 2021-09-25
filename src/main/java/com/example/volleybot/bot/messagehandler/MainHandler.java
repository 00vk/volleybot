package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.cache.TimetableCache;
import com.example.volleybot.bot.cache.VisitCache;
import com.example.volleybot.bot.manager.TimetableManager;
import com.example.volleybot.bot.service.InlineKeyboardService;
import com.example.volleybot.bot.service.SendMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by vkondratiev on 19.09.2021
 * Description:
 */
@Component
public class MainHandler implements IUpdateHandler {

    private final TimetableManager timetableManager;
    private final VisitHandler visitHandler;
    private final PlayerCache playerCache;
    private final TimetableCache timetableCache;
    private final VisitCache visitCache;
    private final InlineKeyboardService keyboardService;
    private final SendMessageService messageService;

    public MainHandler(TimetableManager timetableManager,
                       VisitHandler visitHandler,
                       PlayerCache playerCache,
                       TimetableCache timetableCache,
                       VisitCache visitCache,
                       InlineKeyboardService keyboardService,
                       SendMessageService messageService) {
        this.timetableManager = timetableManager;
        this.visitHandler = visitHandler;
        this.playerCache = playerCache;
        this.timetableCache = timetableCache;
        this.visitCache = visitCache;
        this.keyboardService = keyboardService;
        this.messageService = messageService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage()) {
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        boolean isAdmin = playerCache.isPlayerAdmin(chatId);
        if (isAdmin && "/checkdates" .equalsIgnoreCase(message.getText())) {
            timetableManager.manageDates();
        } else if ("/visit" .equalsIgnoreCase(message.getText())) {
            visitHandler.handleVisitCommand(chatId);
        } else {
            handleAnyMessage(chatId, isAdmin);
        }
    }

    private void handleAnyMessage(Long chatId, boolean isAdmin) {
        messageService.sendMessage(chatId, null, getMainMessage(isAdmin));
    }

    private String getMainMessage(boolean isAdmin) {
        return """
                Доступные команды:
                /visit - записаться на игру
                /settings - настройки
                """;
    }

    @Override
    public BotState state() {
        return BotState.MAIN;
    }
}
