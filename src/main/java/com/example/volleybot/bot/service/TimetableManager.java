package com.example.volleybot.bot.service;

import com.example.volleybot.bot.cache.ReserveCache;
import com.example.volleybot.bot.cache.TimetableCache;
import com.example.volleybot.bot.cache.VisitCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by vkondratiev on 21.09.2021
 * Description:
 */
@Component
public class TimetableManager {

    private final SendMessageService messageService;
    private final TimetableCache timetable;
    private final VisitCache visits;
    private final ReserveCache reserves;

    public TimetableManager(SendMessageService messageService,
                            TimetableCache timetable,
                            VisitCache visits,
                            ReserveCache reserves) {
        this.messageService = messageService;
        this.timetable = timetable;
        this.visits = visits;
        this.reserves = reserves;
    }

    public void manageDates() {
        messageService.log("Выполняется настройка дат");
        addNewDays();
        for (LocalDate date : timetable.getDatesBefore(LocalDate.now())) {
            disableDay(date);
        }
    }

    @Scheduled(cron = "0 0 19 ? * MON") // 19:00:00; ANY<day-of-month>; EVERY<month>; on Mondays
    private void manageOnMondays() {
        manageDates();
        disableDay(LocalDate.now());
        managePinnedMessage();
    }

    public void managePinnedMessage() {
        List<LocalDate> mondays = nextMondays();
        if (mondays.isEmpty())
            return;
        LocalDate nextMonday = mondays.get(0);
        String messageText = visits.listOfPlayers(nextMonday);
        messageService.nextVisitMessage(messageText);
    }

    private void addNewDays() {
        Set<LocalDate> forwardDays = timetable.getDatesAfter(LocalDate.now());
        for (LocalDate nextMonday : nextMondays()) {
            if (forwardDays.contains(nextMonday))
                continue;
            timetable.addNewDate(nextMonday);
        }
    }

    private void disableDay(LocalDate date) {
        reserves.removeReserves(date);
        visits.removeDisabledVisits(date);
        if (timetable.isTimetableEnabled(date)) {
            timetable.disableDay(date);
        }
    }

    public List<LocalDate> nextMondays() {
        List<LocalDate> nextMondays = new ArrayList<>();
        LocalDate nextMonday = LocalDate.now();
        if (nextMonday.getDayOfWeek() == DayOfWeek.MONDAY
                && LocalDateTime.now().isBefore(nextMonday.atTime(19, 0))) {
            nextMondays.add(nextMonday);
        }
        while (nextMondays.size() < 4) {
            nextMonday = nextMonday.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            nextMondays.add(nextMonday);
        }
        return nextMondays;
    }
}
