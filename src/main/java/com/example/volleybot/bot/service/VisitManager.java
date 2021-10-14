package com.example.volleybot.bot.service;

import com.example.volleybot.bot.cache.ReserveCache;
import com.example.volleybot.bot.cache.TimetableCache;
import com.example.volleybot.bot.cache.VisitCache;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;

/**
 * Created by vkondratiev on 13.10.2021
 * Description:
 */
@Component
public class VisitManager {

    private final TimetableCache timetableCache;
    private final VisitCache visitCache;
    private final ReserveCache reserveCache;
    private final SendMessageService messageService;
    private final InlineKeyboardService keyboardService;
    private final TimetableManager timetableManager;

    public VisitManager(TimetableCache timetableCache,
                        VisitCache visitCache,
                        ReserveCache reserveCache,
                        SendMessageService messageService,
                        InlineKeyboardService keyboardService,
                        TimetableManager timetableManager) {
        this.timetableCache = timetableCache;
        this.visitCache = visitCache;
        this.reserveCache = reserveCache;
        this.messageService = messageService;
        this.keyboardService = keyboardService;
        this.timetableManager = timetableManager;
    }

    public void manage(long chatId, long playerId, String handlerName, String dateText) {
        LocalDate date = timetableCache.parse(dateText);
        if (visitCache.isVisitExist(playerId, date)) {
            reserveCache.removeReserve(playerId, date);
        }
        visitCache.switchVisitState(playerId, date);
        reserveCache.manageReserve(date);
        messageService.editKeyboard(chatId, visitKeyboard(handlerName, playerId));
        LocalDate nextMonday = timetableManager.nextMondays().get(0);
        if (nextMonday.isEqual(date)) {
            messageService.updateListMessage(visitCache.listOfPlayers(date));
        }
    }

    public InlineKeyboardMarkup visitKeyboard(String handlerName, long userId) {
        ChooseVisitDateKeyboard keyboard = new ChooseVisitDateKeyboard(timetableCache, visitCache, keyboardService, userId);
        return keyboard.keyboard(handlerName);
    }
}
