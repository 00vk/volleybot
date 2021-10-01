package com.example.volleybot.bot.cache;

import com.example.volleybot.bot.service.SendMessageService;
import com.example.volleybot.db.entity.Player;
import com.example.volleybot.db.entity.Timetable;
import com.example.volleybot.db.entity.Visit;
import com.example.volleybot.db.service.VisitService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Created by vkondratiev on 23.09.2021
 * Description:
 */
@Component
public class VisitCache {

    private final Map<Timetable, Map<Player, Visit>> visits = new HashMap<>();
    private final PlayerCache players;
    private final TimetableCache days;
    private final VisitService service;
    private final SendMessageService messageService;

    public VisitCache(PlayerCache players,
                      TimetableCache days,
                      VisitService service,
                      SendMessageService messageService) {
        this.players = players;
        this.days = days;
        this.service = service;
        this.messageService = messageService;
        fillVisits();
    }

    public void switchVisitState(long playerId, LocalDate date) {
        Player player = players.getPlayer(playerId);
        Timetable timetable = days.getTimetableByDate(date);
        Map<Player, Visit> dayVisits = getDayVisits(timetable);
        Visit visit = getVisit(playerId, date);
        if (visit == null) {
            Visit visit1 = new Visit(player, timetable);
            dayVisits.put(player, visit1);
            messageService.log(player.getName() + " записался на " + days.toText(date));
            service.save(visit1);
        } else {
            dayVisits.remove(player);
            messageService.log(player.getName() + " выписался с " + days.toText(date));
            service.delete(visit);
        }
    }

    public Visit getVisit(long playerId, LocalDate date) {
        Player player = players.getPlayer(playerId);
        return getDayVisits(date).get(player);
    }

    void enableVisit(Visit visitToEnable) {
        visitToEnable.setActive(true);
        service.save(visitToEnable);
    }

    public String listedPlayersOf(LocalDate date) {
        Timetable timetable = days.getTimetableByDate(date);
        Map<Player, Visit> players = visits.getOrDefault(timetable, new HashMap<>());
        Map<Boolean, List<String>> splitByActivityStatus = new HashMap<>();
        splitByActivityStatus.put(true, new ArrayList<>());
        splitByActivityStatus.put(false, new ArrayList<>());
        for (Map.Entry<Player, Visit> player : players.entrySet()) {
            Visit visit = player.getValue();
            splitByActivityStatus.get(visit.isActive()).add(player.getKey().getName());
        }

        int i = 0;
        StringJoiner joiner = new StringJoiner("\n");
        for (String player : splitByActivityStatus.get(true)) {
            joiner.add(++i + ". " + player);
        }
        if (!splitByActivityStatus.get(false).isEmpty())
            joiner.add("----------------------------").add("Запасные:");
        for (String player : splitByActivityStatus.get(false)) {
            joiner.add(++i + ". " + player);
        }

        return joiner.toString();
    }

    Map<Player, Visit> getDayVisits(Timetable timetable) {
        visits.putIfAbsent(timetable, new HashMap<>());
        return visits.get(timetable);
    }

    Map<Player, Visit> getDayVisits(LocalDate date) {
        return getDayVisits(days.getTimetableByDate(date));
    }

    void disableVisit(Visit visitToReserve) {
        visitToReserve.setActive(false);
        service.save(visitToReserve);
    }

    private void fillVisits() {
        List<Visit> allVisits = service.getAll();
        for (Visit visit : allVisits) {
            Player player = visit.getPlayer();
            Timetable timetable = visit.getTimetable();
            Map<Player, Visit> dayVisits = getDayVisits(timetable);
            dayVisits.put(player, visit);
        }
    }
}
