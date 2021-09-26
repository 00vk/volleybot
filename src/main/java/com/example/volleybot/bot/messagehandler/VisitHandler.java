package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.cache.TimetableCache;
import com.example.volleybot.bot.cache.VisitCache;
import com.example.volleybot.bot.service.InlineKeyboardService;
import com.example.volleybot.bot.service.SendMessageService;
import com.example.volleybot.db.entity.Visit;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by vkondratiev on 24.09.2021
 * Description:
 */
@Component
public class VisitHandler implements IUpdateHandler{

    private final SendMessageService messageService;
    private final InlineKeyboardService keyboardService;
    private final PlayerCache playerCache;
    private final TimetableCache timetableCache;
    private final VisitCache visitCache;

    public VisitHandler(SendMessageService messageService,
                        InlineKeyboardService keyboardService,
                        PlayerCache playerCache,
                        TimetableCache timetableCache,
                        VisitCache visitCache) {
        this.messageService = messageService;
        this.keyboardService = keyboardService;
        this.playerCache = playerCache;
        this.timetableCache = timetableCache;
        this.visitCache = visitCache;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasCallbackQuery()) {
            if (!update.hasMessage())
                return;
            Long fromId = update.getMessage().getFrom().getId();
            backToMainMenu(fromId);
            return;
        }

        CallbackQuery callback = update.getCallbackQuery();
        Long chatId = callback.getMessage().getChatId();
        String data = callback.getData();
        if ("/back".equals(data)) {
            backToMainMenu(chatId);
        } else {
            LocalDate date = timetableCache.parse(data);
            visitCache.switchVisitState(chatId, date);
            messageService.editKeyboard(chatId, getKeyboard(chatId));
        }
    }

    private void backToMainMenu(Long chatId) {
        sendMainMessage(chatId, playerCache.isPlayerAdmin(chatId));
        playerCache.setUserBotState(chatId, BotState.MAIN);
    }

    public void handleVisitCommand(Long chatId) {
        messageService.sendMessage(chatId, getKeyboard(chatId), getVisitMessage());
        messageService.log(playerCache.getPlayerName(chatId) + " выбирает дни для записи");
        playerCache.setUserBotState(chatId, BotState.VISIT);
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

    @Override
    public BotState state() {
        return BotState.VISIT;
    }


    private InlineKeyboardMarkup getKeyboard(Long chatId) {
        List<String> texts = new ArrayList<>();
        List<String> callbackData = new ArrayList<>();
        Set<LocalDate> forwardDates = timetableCache.getForwardDates();

        for (LocalDate date : forwardDates) {
            String text = timetableCache.format(date);
            callbackData.add(text);
            Visit visit = visitCache.getVisit(chatId, date);
            if (visit != null) {
                if (visit.isActive()) {
                    text = "✅ " + text + " ✅";
                } else {
                    text = "⚠️ " + text + " ⚠️";
                }
            }
            texts.add(text);
        }
        texts.add("⏪ назад");
        callbackData.add("/back");
        return keyboardService.createKeyboard(2, texts, callbackData);
    }

    private String getVisitMessage() {
        return """
                Выбери дни для записи
                ✅ - дни, на которые ты записан;
                ⚠️ - дни, в которые ты на текущий момент в запасе.
                """;
    }
}
