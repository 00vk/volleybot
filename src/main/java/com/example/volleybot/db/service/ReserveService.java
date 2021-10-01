package com.example.volleybot.db.service;

import com.example.volleybot.db.entity.Reserve;
import com.example.volleybot.db.repository.ReserveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by vkondratiev on 27.09.2021
 * Description:
 */
@Service
public class ReserveService {

    private final ReserveRepository repository;

    public ReserveService(ReserveRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<Reserve> getAll() {
        return repository.findAll();
    }

    @Transactional
    public void addNew(Reserve reserve) {
        repository.save(reserve);
    }

    @Transactional
    public void delete(Reserve reserve) {
        repository.delete(reserve);
    }
}
