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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
    private final Map<Long, Long> actualVisitMessageIds = new HashMap<>();

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
        CallbackQuery callback = update.getCallbackQuery();
        Long chatId = callback.getMessage().getChatId();
        InlineKeyboardMarkup keyboard = callback.getMessage().getReplyMarkup();
        keyboard.getKeyboard().get(0).get(0).setText("ха-ха");
//        callback.getMessage().
        messageService.editKeyboard(chatId, keyboard);
    }

    public void handleVisit(Long chatId) {
        InlineKeyboardMarkup keyboard = keyboardService.createKeyboard(2, getVisitButtons(chatId));
        messageService.sendMessage(chatId, keyboard, getVisitMessage());
        messageService.log(playerCache.getPlayerName(chatId) + " выбирает дни для записи");
        playerCache.setUserBotState(chatId, BotState.VISIT);
    }

    @Override
    public BotState state() {
        return BotState.VISIT;
    }


    private String getVisitMessage() {
        return """
                Выбери дни для записи
                ✅ - дни, на которые ты записан;
                ⚠️ - дни, в которые ты на текущий момент в запасе.""";
    }

    private ArrayList<String> getVisitButtons(Long chatId) {
        Set<String> buttonTexts = new TreeSet<>();
        Set<LocalDate> forwardDates = timetableCache.getForwardDates();

        for (LocalDate date : forwardDates) {
            String text = timetableCache.format(date);
            Visit visit = visitCache.getVisit(chatId, date);
            if (visit != null) {
                if (visit.isActive()) {
                    text = "✅ " + text + " ✅";
                } else {
                    text = "⚠️ " + text + " ⚠️";
                }
            }
            buttonTexts.add(text);
        }
        buttonTexts.add("⏪ назад");

        return new ArrayList<>(buttonTexts);
    }

}
