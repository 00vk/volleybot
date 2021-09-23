package com.example.volleybot.db.repository;

import com.example.volleybot.db.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by vkondratiev on 20.09.2021
 * Description:
 */
public interface PlayerRepository extends JpaRepository<Player, Long> {

}