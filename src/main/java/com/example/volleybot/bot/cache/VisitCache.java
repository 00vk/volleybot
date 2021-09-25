package com.example.volleybot.bot.cache;

import com.example.volleybot.bot.service.SendMessageService;
import com.example.volleybot.db.entity.Player;
import com.example.volleybot.db.entity.Timetable;
import com.example.volleybot.db.entity.Visit;
import com.example.volleybot.db.service.VisitService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final SendMessageService sendMessageService;

    public VisitCache(PlayerCache players,
                      TimetableCache days,
                      VisitService service,
    SendMessageService sendMessageService) {
        this.players = players;
        this.days = days;
        this.service = service;
        this.sendMessageService = sendMessageService;
        List<Visit> allVisits = this.service.getAll();
        for (Visit visit : allVisits) {
            Player player = visit.getPlayer();
            Timetable timetable = visit.getTimetable();
            addVisit(visit, timetable, player);
        }
    }

    private void addVisit(Visit visit, Timetable timetable, Player player) {
        Map<Player, Visit> dayVisits = getDayVisits(timetable);
        dayVisits.put(player, visit);
    }

    private Map<Player, Visit> getDayVisits(Timetable timetable) {
        visits.putIfAbsent(timetable, new HashMap<>());
        return visits.get(timetable);
    }

    public void addVisit(long chatId, LocalDate date) {
        Player player = players.getPlayer(chatId);
        Timetable timetable = days.getTimetableByDate(date);
        Visit visit = new Visit(player, timetable);
        addVisit(visit, timetable, player);
        sendMessageService.log(player.getName() + " записался на " + days.format(date));
        service.addNew(visit);
    }

    public void removeVisit(long chatId, LocalDate date) {
        Player player = players.getPlayer(chatId);
        Timetable timetable = days.getTimetableByDate(date);
        Visit visit = new Visit(player, timetable);
        visits.get(timetable).remove(player);
        sendMessageService.log(player.getName() + " записался на " + days.format(date));
        service.delete(visit);
    }

    public Visit getVisit(long chatId, LocalDate date) {
        Player player = players.getPlayer(chatId);
        Timetable timetable = days.getTimetableByDate(date);
        return getDayVisits(timetable).get(player);
    }
}
