package com.example.volleybot.bot.cache;

import com.example.volleybot.db.entity.Player;
import com.example.volleybot.db.entity.Reserve;
import com.example.volleybot.db.entity.Timetable;
import com.example.volleybot.db.entity.Visit;
import com.example.volleybot.db.service.ReserveService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by vkondratiev on 27.09.2021
 * Description:
 */
@Component
public class ReserveCache {

    private final Map<LocalDate, Set<Reserve>> reserves = new HashMap<>();
    private final ReserveService service;
    private final TimetableCache days;
    private final VisitCache visits;

    public ReserveCache(ReserveService service,
                        TimetableCache days,
                        VisitCache visits) {
        this.service = service;
        this.days = days;
        this.visits = visits;
        fillReserves();
    }

    private List<Visit> getReservedVisits(LocalDate date) {
        return getDayReserves(date).stream()
                                   .map(Reserve::getVisit)
                                   .collect(Collectors.toList());
    }

    private void addReserve(LocalDate date, Visit visit, LocalDate lastAbsentDate) {
        Set<Reserve> dayReserves = getDayReserves(date);
        Reserve reserve = new Reserve(visit, lastAbsentDate);
        dayReserves.add(reserve);
        service.addNew(reserve);
    }

    private void deleteFirstReserve(LocalDate date) {
        Iterator<Reserve> dayReserves = getDayReserves(date).iterator();
        if (!dayReserves.hasNext())
            return;
        Reserve nextReserve = dayReserves.next();
        dayReserves.remove();
        service.delete(nextReserve);
    }

    private Set<Reserve> getDayReserves(LocalDate date) {
        reserves.putIfAbsent(date, new TreeSet<>());
        return this.reserves.get(date);
    }

    public void manageReserve(LocalDate date) {
        List<Visit> reservedVisits = getReservedVisits(date);
        Set<Visit> activeVisits = visits.getDayVisits(date).values().stream()
                                        .filter(Visit::isActive)
                                        .collect(Collectors.toSet());
        while (activeVisits.size() < days.getPlayersLimit(date) && !reservedVisits.isEmpty()) {
            enableVisit(date, reservedVisits.get(0), activeVisits);
        }
        while (activeVisits.size() > days.getPlayersLimit(date)) {
            disableVisit(date, activeVisits);
        }
    }

    private void disableVisit(LocalDate date, Set<Visit> activeVisits) {
        List<Player> candidatesToReserve = activeVisits.stream()
                                                       .map(Visit::getPlayer)
                                                       .collect(Collectors.toList());
        List<Timetable> previousDays = days.getDaysBefore(date);
        Player reservePlayer = null;
        LocalDate lastAbsentDate = null;
        while (!previousDays.isEmpty() && !candidatesToReserve.isEmpty()) {
            Timetable previousDay = previousDays.remove(0);
            lastAbsentDate = previousDay.getGameDate();
            for (Player player : visits.getDayVisits(previousDay).keySet().stream()
                                       .filter(candidatesToReserve::contains)
                                       .collect(Collectors.toSet())) {
                reservePlayer = player;
                candidatesToReserve.remove(reservePlayer);
            }
        }
        if (lastAbsentDate == null) {
            lastAbsentDate = LocalDate.of(2021, 1, 1);
        }
        reservePlayer = candidatesToReserve.stream().findAny().orElse(reservePlayer);
        Visit visitToReserve = visits.getDayVisits(date).get(reservePlayer);
        activeVisits.remove(visitToReserve);
        visits.disableVisit(visitToReserve);
        addReserve(date, visitToReserve, lastAbsentDate);
    }

    private void enableVisit(LocalDate date, Visit visitToEnable, Set<Visit> activeVisits) {
        activeVisits.add(visitToEnable);
        visits.enableVisit(visitToEnable);
        deleteFirstReserve(date);
    }


    private void fillReserves() {
        List<Reserve> allReserves = service.getAll();
        for (Reserve reserve : allReserves) {
            LocalDate date = reserve.getVisit().getTimetable().getGameDate();
            Set<Reserve> dayReserves = getDayReserves(date);
            dayReserves.add(reserve);
        }
    }
}
