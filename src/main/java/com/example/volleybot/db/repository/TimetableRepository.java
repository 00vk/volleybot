package com.example.volleybot.db.repository;

import com.example.volleybot.db.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by vkondratiev on 20.09.2021
 * Description:
 */
public interface TimetableRepository extends JpaRepository<Timetable, Integer> {

}
