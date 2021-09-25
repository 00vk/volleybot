package com.example.volleybot.db.service;

import com.example.volleybot.db.entity.Timetable;
import com.example.volleybot.db.repository.TimetableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by vkondratiev on 20.09.2021
 * Description:
 */
@Service
public class TimetableService {

    private final TimetableRepository repository;

    public TimetableService(TimetableRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void disableDay(int id) {
        Timetable timetable = repository.getById(id);
        timetable.setEnabled(false);
        repository.save(timetable);
    }

    @Transactional
    public List<Timetable> getAllDays() {
        return repository.findAll();
    }

    @Transactional
    public void save(Timetable timetable) {
        repository.save(timetable);
    }
}
