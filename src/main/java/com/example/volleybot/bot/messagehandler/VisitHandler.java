package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.service.SendMessageService;
import com.example.volleybot.bot.service.VisitManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Created by vkondratiev on 24.09.2021
 * Description:
 */
@Component
public class VisitHandler implements IUpdateHandler {

    private final SendMessageService messageService;
    private final PlayerCache playerCache;
    private final VisitManager visitManager;

    public VisitHandler(SendMessageService messageService,
                        PlayerCache playerCache,
                        VisitManager visitManager) {
        this.messageService = messageService;
        this.playerCache = playerCache;
        this.visitManager = visitManager;
    }

    @Override
    public boolean handle(Message message) {
        long userId = message.getFrom().getId();
        if (!message.hasText())
            return true;
        String messageText = message.getText();
        if ("/visit".equals(messageText)) {
            InlineKeyboardMarkup keyboard = visitManager.visitKeyboard("/visit", userId);
            messageService.sendUpdatableMessage(userId, keyboard, getVisitMessage());
            messageService.log(playerCache.getPlayerName(userId) + " выбирает дни для записи");
            return true;
        }
        playerCache.setUserBotState(userId, BotState.of(messageText));
        return false;
    }

    @Override
    public boolean handle(CallbackQuery callback) {
        long userId = callback.getFrom().getId();
        String[] commands = callback.getData().split(" ");
        if ("/main".equals(commands[0])) {
            playerCache.setUserBotState(userId, BotState.MAIN);
            return false;
        }
        visitManager.manage(userId, userId, "/visit", commands[2]);
        return true;
    }

    @Override
    public BotState state() {
        return BotState.VISIT;
    }

    private String getVisitMessage() {
        return """
                📆 Выбери дни для записи
                ✅ - дни, на которые ты записан;
                ⚠️ - дни, в которые ты на текущий момент в запасе.
                """;
    }
}
