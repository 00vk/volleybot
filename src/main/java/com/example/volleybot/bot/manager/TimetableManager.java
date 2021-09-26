package com.example.volleybot.bot.manager;

import com.example.volleybot.bot.cache.TimetableCache;
import com.example.volleybot.bot.cache.VisitCache;
import com.example.volleybot.bot.service.SendMessageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
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

    public TimetableManager(SendMessageService messageService,
                            TimetableCache timetable,
                            VisitCache visits) {
        this.messageService = messageService;
        this.timetable = timetable;
        this.visits = visits;
    }

    public void manageDates() {
        messageService.log("Выполняется настройка дат");
        addNewDays();
        disableOldDays();
    }

    private void disableOldDays() {
        Set<LocalDate> oldDates = timetable.getOldEnabledDates();
        for (LocalDate date : oldDates) {
            timetable.disableDay(date);
        }
    }

    private void addNewDays() {
        Set<LocalDate> forwardDays = timetable.getForwardDates();
        for (LocalDate nextMonday : nextMondays()) {
            if (forwardDays.contains(nextMonday))
                continue;
            timetable.addNewDate(nextMonday);
        }
    }

    @Scheduled(cron = "0 0 19 ? * MON") // 19:00:00; ANY<day-of-month>; EVERY<month>; on Mondays
    private void manageOnMondays() {
        manageDates();
        timetable.disableDay(LocalDate.now());
        managePinnedMessage();
    }

    public void managePinnedMessage() {
        List<LocalDate> mondays = nextMondays();
        if (mondays.isEmpty())
            return;
        LocalDate nextMonday = mondays.get(0);
        String messageText = pinnedMessageText(nextMonday);
        messageService.nextVisitMessage(messageText);
    }

    private String pinnedMessageText(LocalDate nextMonday) {
        String dateText = timetable.format(nextMonday);
        return "\uD83C\uDFD0 " + dateText + " \uD83C\uDFD0:\n" + visits.listedPlayersOf(nextMonday);
    }

    private List<LocalDate> nextMondays() {
        List<LocalDate> nextMondays = new ArrayList<>();
        LocalDate nextMonday = LocalDate.now();
        for (int i = 0; i < 4; i++) {
            nextMonday = nextMonday.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            nextMondays.add(nextMonday);
        }
        return nextMondays;
    }
}
