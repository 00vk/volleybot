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
import java.util.stream.Collectors;

/**
 * Created by vkondratiev on 23.09.2021
 * Description:
 */
@Component
public class VisitCache {

    private final Map<Timetable, Map<Player, Visit>> visits = new HashMap<>();
    private final PlayerCache playerCache;
    private final TimetableCache timetableCache;
    private final VisitService service;
    private final SendMessageService messageService;

    public VisitCache(PlayerCache playerCache,
                      TimetableCache timetableCache,
                      VisitService service,
                      SendMessageService messageService) {
        this.playerCache = playerCache;
        this.timetableCache = timetableCache;
        this.service = service;
        this.messageService = messageService;
        fillVisits();
    }

    public boolean isVisitExist(long playerId, LocalDate date) {
        return getVisit(playerId, date) != null;
    }

    public boolean isVisitActive(long playerId, LocalDate date) {
        return getVisit(playerId, date).isActive();
    }

    public void switchVisitState(long playerId, LocalDate date) {
        Player player = playerCache.getPlayer(playerId);
        Timetable timetable = timetableCache.getTimetableByDate(date);
        Map<Player, Visit> dayVisits = getDayVisits(timetable);
        Visit visit = getVisit(playerId, date);
        if (visit == null) {
            Visit newVisit = new Visit(player, timetable);
            dayVisits.put(player, newVisit);
            messageService.log(player.getName() + " записался на " + timetableCache.toText(date));
            service.save(newVisit);
        } else {
            dayVisits.remove(player);
            messageService.log(player.getName() + " не сможет прийти " + timetableCache.toText(date));
            service.delete(visit);
        }
    }

    private Visit getVisit(long playerId, LocalDate date) {
        Player player = playerCache.getPlayer(playerId);
        return getDayVisits(date).get(player);
    }

    public String listOfPlayers(LocalDate date) {
        Timetable timetable = timetableCache.getTimetableByDate(date);
        Map<Player, Visit> players = visits.getOrDefault(timetable, new HashMap<>());
        Map<Boolean, List<String>> splitByActivityStatus = new HashMap<>();
        splitByActivityStatus.put(true, new ArrayList<>());
        splitByActivityStatus.put(false, new ArrayList<>());
        for (Map.Entry<Player, Visit> player : players.entrySet()) {
            Visit visit = player.getValue();
            splitByActivityStatus.get(visit.isActive()).add(player.getKey().getName());
        }

        int i = 0;
        String dateText = timetableCache.toText(date);
        String prefix = "\uD83C\uDFD0 " + dateText + " \uD83C\uDFD0\nУчастники:\n";
        StringJoiner joiner = new StringJoiner("\n", prefix, "");
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

    public void removeDisabledVisits(LocalDate date) {
        Map<Player, Visit> dayVisits = getDayVisits(date);
        Map<Player, Visit> toRemove = dayVisits.entrySet().stream()
                                               .filter(visit -> !visit.getValue().isActive())
                                               .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        for (Map.Entry<Player, Visit> each : toRemove.entrySet()) {
            dayVisits.remove(each.getKey());
            service.delete(each.getValue());
        }
    }

    Map<Player, Visit> getDayVisits(Timetable timetable) {
        visits.putIfAbsent(timetable, new HashMap<>());
        return visits.get(timetable);
    }

    Map<Player, Visit> getDayVisits(LocalDate date) {
        return getDayVisits(timetableCache.getTimetableByDate(date));
    }

    void enableVisit(Visit visitToEnable) {
        visitToEnable.setActive(true);
        service.save(visitToEnable);


        LocalDate date = visitToEnable.getTimetable().getGameDate();
        Player player = visitToEnable.getPlayer();
        String dateText = timetableCache.toText(date);
        messageService.log(dateText + ": " + player.getName() + " вновь приглашен на игру");
//        messageService.sendMessage(/*player.getId()*/90246220, null, fromReserveMessage(dateText));
    }

    void disableVisit(Visit visitToReserve) {
        visitToReserve.setActive(false);
        service.save(visitToReserve);

        LocalDate date = visitToReserve.getTimetable().getGameDate();
        Player player = visitToReserve.getPlayer();
        String dateText = timetableCache.toText(date);
        messageService.log(dateText + ": " + player.getName() + " перемещен в запас");
//        messageService.sendMessage(/*player.getId()*/90246220, null, intoReserveMessage(dateText));
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

    private String intoReserveMessage(String dateText) {
        return "⚠️ " + dateText + " ⚠️\n"
                + "К сожалению, ты в запасе на указанное число.\n"
                + "Если кто-то отменит свою запись на эту дату, ты автоматически будешь записан обратно.\n"
                + "Используй команду /visit для управления своей записью.\n"
                + "Для просмотра списка записавшихся используй команду /list.";
    }

    private String fromReserveMessage(String dateText) {
        return "✅ " + dateText + " ✅\n"
                + "Кто-то отменил свою запись, и ты снова в числе участников!\n"
                + "Приходи, ждем тебя на игре!\n"
                + "Используй команду /visit для управления своей записью.\n"
                + "Для просмотра списка записавшихся используй команду /list.";
    }
}
