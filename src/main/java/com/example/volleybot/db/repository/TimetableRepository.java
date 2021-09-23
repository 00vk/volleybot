package com.example.volleybot.db.repository;

import com.example.volleybot.db.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by vkondratiev on 20.09.2021
 * Description:
 */
public interface TimetableRepository extends JpaRepository<Timetable, Integer> {

    List<Timetable> findTimetableByEnabledTrueAndPlayersLimitGreaterThan(int playersLimit);
}
