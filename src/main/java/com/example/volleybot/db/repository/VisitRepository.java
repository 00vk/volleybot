package com.example.volleybot.db.repository;

import com.example.volleybot.db.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by vkondratiev on 23.09.2021
 * Description:
 */
public interface VisitRepository extends JpaRepository<Visit, Integer> {

}
