package com.example.volleybot.bot.cache;

import com.example.volleybot.db.entity.Player;
import com.example.volleybot.db.entity.Reserve;
import com.example.volleybot.db.entity.Timetable;
import com.example.volleybot.db.entity.Visit;
import com.example.volleybot.db.service.ReserveService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by vkondratiev on 27.09.2021
 * Description:
 */
@Component
public class ReserveCache {

    private final Map<LocalDate, Map<Visit, Reserve>> reserves = new HashMap<>();
    private final ReserveService service;
    private final TimetableCache timetableCache;
    private final VisitCache visitCache;
    private final PlayerCache playerCache;

    public ReserveCache(ReserveService service,
                        PlayerCache playerCache,
                        TimetableCache timetableCache,
                        VisitCache visitCache) {
        this.service = service;
        this.timetableCache = timetableCache;
        this.visitCache = visitCache;
        this.playerCache = playerCache;
        fillReserves();
    }

    public void manageReserve(LocalDate date) {
        List<Visit> reservedVisits = getReservedVisits(date);
        Set<Visit> activeVisits = visitCache.getDayVisits(date).values().stream()
                                            .filter(Visit::isActive)
                                            .collect(Collectors.toSet());
        while (activeVisits.size() < timetableCache.getPlayersLimit(date) && !reservedVisits.isEmpty()) {
            enableVisit(date, reservedVisits.get(0), activeVisits);
        }
        while (activeVisits.size() > timetableCache.getPlayersLimit(date)) {
            disableVisit(date, activeVisits);
        }
    }

    public void removeReserve(Long userId, LocalDate date) {
        Player player = playerCache.getPlayer(userId);
        Visit visit = visitCache.getDayVisits(date).get(player);
        Map<Visit, Reserve> dayReserves = getDayReserves(date);
        if (!dayReserves.containsKey(visit)) {
            return;
        }
        Reserve reserve = dayReserves.remove(visit);
        service.delete(reserve);
    }

    public void removeReserves(LocalDate date) {
        Map<Visit, Reserve> todayReserves = getDayReserves(date);
        for (Reserve reserve : todayReserves.values()) {
            service.delete(reserve);
        }
        todayReserves.clear();
    }

    private List<Visit> getReservedVisits(LocalDate date) {
        Set<Visit> visits = getDayReserves(date).keySet();
        return new ArrayList<>(visits);
    }

    private Reserve getFirstReserve(LocalDate date) {
        Map<Visit, Reserve> dayReserves = getDayReserves(date);
        if (dayReserves.isEmpty())
            return null;
        return Collections.min(dayReserves.values());
    }

    private Map<Visit, Reserve> getDayReserves(LocalDate date) {
        reserves.putIfAbsent(date, new HashMap<>());
        return this.reserves.get(date);
    }

    private void disableVisit(LocalDate date, Set<Visit> activeVisits) {
        List<Player> candidatesToReserve = activeVisits.stream()
                                                       .map(Visit::getPlayer)
                                                       .collect(Collectors.toList());
        List<Timetable> previousDays = timetableCache.getDaysBefore(date);
        Player reservePlayer = null;
        LocalDate lastAbsentDate = null;
        while (!previousDays.isEmpty() && !candidatesToReserve.isEmpty()) {
            Timetable previousDay = previousDays.remove(0);
            lastAbsentDate = previousDay.getGameDate();
            for (Player player : visitCache.getDayVisits(previousDay)
                                           .entrySet().stream()
                                           .filter(e -> e.getValue().isActive())
                                           .map(Map.Entry::getKey)
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
        Visit visitToReserve = visitCache.getDayVisits(date).get(reservePlayer);
        activeVisits.remove(visitToReserve);
        visitCache.disableVisit(visitToReserve);
        addReserve(date, visitToReserve, lastAbsentDate);
    }

    private void addReserve(LocalDate date, Visit visit, LocalDate lastAbsentDate) {
        Map<Visit, Reserve> dayReserves = getDayReserves(date);
        Reserve reserve = new Reserve(visit, lastAbsentDate);
        dayReserves.put(visit, reserve);
        service.addNew(reserve);
    }

    private void enableVisit(LocalDate date, Visit visitToEnable, Set<Visit> activeVisits) {
        activeVisits.add(visitToEnable);
        visitCache.enableVisit(visitToEnable);
        deleteFirstReserve(date);
    }

    private void deleteFirstReserve(LocalDate date) {
        Reserve firstReserve = getFirstReserve(date);
        if (firstReserve == null)
            return;
        Map<Visit, Reserve> dayReserves = getDayReserves(date);
        dayReserves.remove(firstReserve.getVisit());
        service.delete(firstReserve);
    }

    @PostConstruct
    private void fillReserves() {
        List<Reserve> allReserves = service.getAll();
        for (Reserve reserve : allReserves) {
            Visit visit = reserve.getVisit();
            LocalDate date = visit.getTimetable().getGameDate();
            Map<Visit, Reserve> dayReserves = getDayReserves(date);
            dayReserves.put(visit, reserve);
        }
    }
}
