package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.cache.TimetableCache;
import com.example.volleybot.bot.service.SendMessageService;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vkondratiev on 21.09.2021
 * Description:
 */
@Component
public class TimetableManager {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final SendMessageService sendMessageService;
    private final TimetableCache timetableCache;

    public TimetableManager(SendMessageService sendMessageService, TimetableCache timetableCache) {
        this.sendMessageService = sendMessageService;
        this.timetableCache = timetableCache;
    }

    public void manageDates() {
        sendMessageService.log("Настройка дат");
        Set<LocalDate> oldDates = timetableCache.getOldEnabledDates();
        for (LocalDate date : oldDates) {
            disableDate(date);
        }

        Set<LocalDate> forwardDays = timetableCache.getForwardDates();
        for (LocalDate nextMonday : nextMondays()) {
            if (forwardDays.contains(nextMonday))
                continue;
            addNewDate(nextMonday);
        }
    }

    private void addNewDate(LocalDate date) {
        String dateText = date.format(FORMATTER);
        sendMessageService.log("Добавлена новая дата: " + dateText);
        timetableCache.addNewDate(date);
    }

    public void disableDate(LocalDate date) {
        String dateText = date.format(FORMATTER);
        sendMessageService.log("Дата " + dateText + " убрана из доступа");
        timetableCache.disableDay(date);
    }

    private Set<LocalDate> nextMondays() {
        Set<LocalDate> nextMondays = new HashSet<>();
        LocalDate nextMonday = LocalDate.now();
        for (int i = 0; i < 4; i++) {
            nextMonday = nextMonday.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            nextMondays.add(nextMonday);
        }
        return nextMondays;
    }

}
