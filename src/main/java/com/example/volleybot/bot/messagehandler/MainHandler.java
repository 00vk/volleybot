package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.manager.TimetableManager;
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
    private final SendMessageService messageService;
    private final RecordHandler recordHandler;

    public MainHandler(TimetableManager timetableManager,
                       PlayerCache playerCache,
                       SendMessageService messageService,
                       VisitHandler visitHandler,
                       RecordHandler recordHandler) {
        this.timetableManager = timetableManager;
        this.visitHandler = visitHandler;
        this.playerCache = playerCache;
        this.messageService = messageService;
        this.recordHandler = recordHandler;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage()) {
            return;
        }
        Message message = update.getMessage();
        Long userId = message.getFrom().getId();
        boolean isAdmin = playerCache.isPlayerAdmin(userId);
        String messageText = message.getText();
        String[] split = messageText.split("[ @]");
        String command = split[0];
        if (isAdmin && "/checkdates".equalsIgnoreCase(command)) {
            timetableManager.manageDates();
        } else if ("/visit".equalsIgnoreCase(command)) {
            visitHandler.handleVisitCommand(userId);
        } else if (isAdmin && "/pin".equalsIgnoreCase(command)) {
            timetableManager.managePinnedMessage();
        } else if (isAdmin && "/addNewPlayer".equalsIgnoreCase(command)) {
            addNewPlayer(userId, split);
        } else if (isAdmin && "/record".equalsIgnoreCase(command)) {
            recordHandler.handleRecord(userId);
        } else {
            handleAnyMessage(userId, isAdmin);
        }
    }

    private void addNewPlayer(long adminId, String[] split) {
        if (split.length < 3) return;
        long userId;
        try {
            userId = Long.parseLong(split[1]);
        } catch (NumberFormatException e) {
            messageService.log("ОШИБКА! Невозможно добавить пользователя - некорректный user_id: " + split[1]);
            return;
        }
        String admin = playerCache.getPlayerName(adminId);
        playerCache.addNewPlayer(userId, split[2], false);
        messageService.log(admin + " добавил нового пользователя: " + split[2] + " (id" + userId + ")");
    }

    private void handleAnyMessage(Long id, boolean isAdmin) {
        messageService.sendMessage(id, null, getMainMessage(isAdmin));
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
