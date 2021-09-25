package com.example.volleybot.db.service;

import com.example.volleybot.db.entity.Player;
import com.example.volleybot.db.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by vkondratiev on 11.09.2021
 * Description:
 */

@Service
public class PlayerService {

    private final PlayerRepository repository;

    public PlayerService(PlayerRepository repository) {
        this.repository = repository;
    }

    public List<Player> getAllPlayers() {
        return repository.findAll();
    }

    public Player getById(Long chatId) {
        return repository.getById(chatId);
    }

    public void addNewPlayer(Long chatId, String playerName, boolean isAdmin) {
        Player player = new Player(chatId);
        player.setName(playerName);
        player.setAdmin(isAdmin);
        repository.save(player);
    }
}
