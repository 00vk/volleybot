package com.example.volleybot.service;

import com.example.volleybot.entity.Player;
import com.example.volleybot.repository.PlayerRepository;
import org.springframework.stereotype.Service;

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

    public Player findPlayer(long chatId) {
        return repository.findByChatId(chatId);
    }

}
