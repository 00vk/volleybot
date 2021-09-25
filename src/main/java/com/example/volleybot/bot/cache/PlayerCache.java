package com.example.volleybot.bot.cache;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.db.entity.Player;
import com.example.volleybot.db.entity.Visit;
import com.example.volleybot.db.service.PlayerService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
@Component
public class PlayerCache {

    private final Map<Long, Player> players = new HashMap<>();
    private final Map<Player, BotState> playerBotStates = new HashMap<>();
    private final PlayerService service;

    public PlayerCache(PlayerService service) {
        this.service = service;
        this.service.getAllPlayers()
                    .forEach(player -> {
                         players.put(player.getId(), player);
                         playerBotStates.put(player, BotState.MAIN);
                     });
    }

    public void setUserBotState(long chatId, BotState botState) {
        this.playerBotStates.put(players.get(chatId), botState);
    }

    public BotState botState(long chatId) {
        Player player = getPlayer(chatId);
        playerBotStates.putIfAbsent(player, BotState.START);
        return playerBotStates.get(player);
    }

    Player getPlayer(long chatId) {
        players.putIfAbsent(chatId, new Player(chatId));
        return players.get(chatId);
    }

    public void updatePlayer(Long chatId, String playerName, boolean isAdmin) {
        Player player = players.get(chatId);
        player.setName(playerName);
        player.setAdmin(isAdmin);
    }

    public void updatePlayer(Long chatId, String telegramName) {
        updatePlayer(chatId, telegramName, false);
    }

    public boolean isPlayerAdmin(Long chatId) {
        return getPlayer(chatId).isAdmin();
    }

    public String getPlayerName(Long chatId) {
        return getPlayer(chatId).getName();
    }

    public void addNewPlayer(Long chatId, String playerName, boolean isAdmin) {
        updatePlayer(chatId, playerName, isAdmin);
        service.addNewPlayer(chatId, playerName, isAdmin);
    }

    public Set<Visit> getVisits(long chatId) {
        Player player = getPlayer(chatId);
        return new TreeSet<>(player.getVisits());
    }

}
