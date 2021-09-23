package com.example.volleybot.bot.cache;

import com.example.volleybot.db.entity.Timetable;
import com.example.volleybot.db.service.TimetableService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by vkondratiev on 22.09.2021
 * Description:
 */
@Component
public class TimetableCache {

    private final TimetableService service;
    private final Map<LocalDate, Timetable> allDays;

    public TimetableCache(TimetableService service) {
        this.service = service;
        allDays = service.getAllDays().stream()
                         .collect(Collectors.toConcurrentMap(Timetable::getGameDate, t -> t, (t1, t2) -> t2));

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
        allDays.putIfAbsent(date, new Timetable(date));
        Timetable timetable = allDays.get(date);
        timetable.setEnabled(false);
        service.save(timetable);
    }

    public void addNewDate(LocalDate date) {
        allDays.putIfAbsent(date, new Timetable(date));
        Timetable timetable = allDays.get(date);
        service.save(timetable);
    }
}
