package com.example.volleybot.bot.cache;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.db.entity.Player;
import com.example.volleybot.db.service.PlayerService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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

    public void setUserBotState(long id, BotState botState) {
        this.playerBotStates.put(players.get(id), botState);
    }

    public BotState botState(long id) {
        Player player = getPlayer(id);
        playerBotStates.putIfAbsent(player, BotState.START);
        return playerBotStates.get(player);
    }

    Player getPlayer(long id) {
        players.putIfAbsent(id, new Player(id));
        return players.get(id);
    }

    private void updatePlayer(Long id, String playerName, boolean isAdmin) {
        Player player = players.get(id);
        player.setName(playerName);
        player.setAdmin(isAdmin);
    }

    public void updatePlayer(Long id, String telegramName) {
        updatePlayer(id, telegramName, false);
    }

    public boolean isPlayerAdmin(Long id) {
        return getPlayer(id).isAdmin();
    }

    public String getPlayerName(Long id) {
        return getPlayer(id).getName();
    }

    public void addNewPlayer(Long id, String playerName, boolean isAdmin) {
        updatePlayer(id, playerName, isAdmin);
        service.addNewPlayer(id, playerName, isAdmin);
    }
}
