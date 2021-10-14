package com.example.volleybot.bot.service;

import com.example.volleybot.bot.cache.TimetableCache;
import com.example.volleybot.bot.cache.VisitCache;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by vkondratiev on 13.10.2021
 * Description:
 */
public record ChooseVisitDateKeyboard(TimetableCache timetableCache,
                                      VisitCache visitCache,
                                      InlineKeyboardService keyboardService,
                                      long userId) {

    public InlineKeyboardMarkup keyboard(String handlerName) {
        List<String> texts = new ArrayList<>();
        List<String> callbackData = new ArrayList<>();
        Set<LocalDate> forwardDates = timetableCache.getDatesAfter(LocalDate.now());

        for (LocalDate date : forwardDates) {
            String text = timetableCache.toText(date);
            callbackData.add(handlerName + " date " + timetableCache.format(date));
            if (visitCache.isVisitExist(userId, date)) {
                if (visitCache.isVisitActive(userId, date)) {
                    text = "✅ " + text + " ✅";
                } else {
                    text = "⚠️ " + text + " ⚠️";
                }
            }
            texts.add(text);
        }
        return keyboardService.createKeyboard(2, texts, callbackData);
    }
}
