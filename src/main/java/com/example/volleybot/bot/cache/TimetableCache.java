package com.example.volleybot.bot.cache;

import com.example.volleybot.bot.service.SendMessageService;
import com.example.volleybot.db.entity.Timetable;
import com.example.volleybot.db.entity.Visit;
import com.example.volleybot.db.service.TimetableService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by vkondratiev on 22.09.2021
 * Description:
 */
@Component
public class TimetableCache {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMMM");
    private final TimetableService service;
    private final Map<LocalDate, Timetable> allDays;
    private final SendMessageService sendMessageService;

    public TimetableCache(TimetableService service, SendMessageService sendMessageService) {
        this.service = service;
        this.sendMessageService = sendMessageService;
        allDays = service.getAllDays().stream()
                         .collect(Collectors.toMap(Timetable::getGameDate, t -> t, (t1, t2) -> t2, TreeMap::new));

    }

    public Set<LocalDate> getOldEnabledDates() {
        return allDays.entrySet().stream()
                      .filter(e -> e.getValue().isEnabled())
                      .map(Map.Entry::getKey)
                      .filter(day -> day.isBefore(LocalDate.now()))
                      .collect(Collectors.toUnmodifiableSet());
    }

    public Set<LocalDate> getForwardDates() {
        return allDays.keySet().stream()
                      .filter(date -> date.isAfter(LocalDate.now()))
                      .collect(Collectors.toSet());
    }

    public void disableDay(LocalDate date) {
        sendMessageService.log("Дата " + format(date) + " убрана из доступа");
        allDays.putIfAbsent(date, new Timetable(date));
        Timetable timetable = allDays.get(date);
        timetable.setEnabled(false);
        service.save(timetable);
    }

    public void addNewDate(LocalDate date) {
        allDays.putIfAbsent(date, new Timetable(date));
        Timetable timetable = allDays.get(date);
        sendMessageService.log("Добавлена новая дата: " + format(date));
        service.save(timetable);
    }

    Timetable getTimetableByDate(LocalDate date) {
        return allDays.get(date);
    }

    public Set<Visit> getVisits(LocalDate date) {
        return getTimetableByDate(date).getVisits();
    }

    public String format(LocalDate date) {
        return FORMATTER.format(date);
    }

    public LocalDate parse(String dateString) {
        return LocalDate.parse(dateString, FORMATTER);
    }
}
