package com.example.volleybot.db.service;

import com.example.volleybot.db.entity.Visit;
import com.example.volleybot.db.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by vkondratiev on 23.09.2021
 * Description:
 */
@Service
public class VisitService {

    private final VisitRepository repository;

    public VisitService(VisitRepository repository) {
        this.repository = repository;
    }

    public List<Visit> getAll() {
        return repository.findAll();
    }

    public void addNew(Visit visit) {
        repository.save(visit);
    }

    public void delete(Visit visit){
        repository.delete(visit);
    }
}