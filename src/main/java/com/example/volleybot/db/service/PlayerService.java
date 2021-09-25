package com.example.volleybot.db.service;

import com.example.volleybot.db.entity.Player;
import com.example.volleybot.db.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public List<Player> getAllPlayers() {
        return repository.findAll();
    }

    @Transactional
    public void addNewPlayer(Long chatId, String playerName, boolean isAdmin) {
        Player player = new Player(chatId);
        player.setName(playerName);
        player.setAdmin(isAdmin);
        repository.save(player);
    }
}
