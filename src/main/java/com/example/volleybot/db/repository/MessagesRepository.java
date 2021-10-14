package com.example.volleybot.db.repository;

import com.example.volleybot.db.entity.PinnedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by vkondratiev on 08.10.2021
 * Description:
 */
public interface MessagesRepository extends JpaRepository<PinnedMessage, Long> {

}
