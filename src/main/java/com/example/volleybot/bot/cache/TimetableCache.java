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
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by vkondratiev on 22.09.2021
 * Description:
 */
@Component
public class TimetableCache {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MM yyyy");
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
                      .collect(Collectors.toCollection(TreeSet::new));
    }

    public void disableDay(LocalDate date) {
        sendMessageService.log("Дата " + format(date) + " убрана из доступа");
        Timetable timetable = getTimetable(date);
        timetable.setEnabled(false);
        service.save(timetable);
    }

    public void addNewDate(LocalDate date) {
        Timetable timetable = getTimetable(date);
        sendMessageService.log("Добавлена новая дата: " + format(date));
        service.save(timetable);
    }

    Timetable getTimetableByDate(LocalDate date) {
        return allDays.get(date);
    }

    private Timetable getTimetable(LocalDate date) {
        allDays.putIfAbsent(date, new Timetable(date));
        return allDays.get(date);
    }

    public String format(LocalDate date) {
        return FORMATTER.format(date);
    }

    public LocalDate parse(String dateString) {
        return LocalDate.parse(dateString, FORMATTER);
    }
}
