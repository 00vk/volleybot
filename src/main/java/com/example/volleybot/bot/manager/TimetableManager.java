package com.example.volleybot.bot.manager;

import com.example.volleybot.bot.cache.TimetableCache;
import com.example.volleybot.bot.service.SendMessageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vkondratiev on 21.09.2021
 * Description:
 */
@Component
public class TimetableManager {

    private final SendMessageService sendMessageService;
    private final TimetableCache timetableCache;

    public TimetableManager(SendMessageService sendMessageService, TimetableCache timetableCache) {
        this.sendMessageService = sendMessageService;
        this.timetableCache = timetableCache;
    }

    public void manageDates() {
        sendMessageService.log("Выполняется настройка дат");
        addNewDays();
        disableOldDays();
    }

    private void disableOldDays() {
        Set<LocalDate> oldDates = timetableCache.getOldEnabledDates();
        for (LocalDate date : oldDates) {
            timetableCache.disableDay(date);
        }
    }

    private void addNewDays() {
        Set<LocalDate> forwardDays = timetableCache.getForwardDates();
        for (LocalDate nextMonday : nextMondays()) {
            if (forwardDays.contains(nextMonday))
                continue;
            timetableCache.addNewDate(nextMonday);
        }
    }

    @Scheduled(cron = "0 0 19 ? * MON") // 19:00:00; ANY<day-of-month>; EVERY<month>; on Mondays
    private void manageOnMondays() {
        manageDates();
        timetableCache.disableDay(LocalDate.now());
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
