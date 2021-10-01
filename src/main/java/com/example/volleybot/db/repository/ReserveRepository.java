package com.example.volleybot.db.repository;

import com.example.volleybot.db.entity.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by vkondratiev on 27.09.2021
 * Description:
 */
public interface ReserveRepository extends JpaRepository<Reserve, Long> {

}
