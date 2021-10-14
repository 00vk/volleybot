package com.example.volleybot.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDate;

/**
 * Created by vkondratiev on 27.09.2021
 * Description:
 */
@Entity
public class Reserve implements Comparable<Reserve>{

    @Id @Column(name = "id", nullable = false)
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "visit_id")
    private Visit visit;

    private LocalDate lastAbsentDate;

    public Reserve() {
    }

    public Reserve(Visit visit, LocalDate lastAbsentDate) {
        this.visit = visit;
        this.lastAbsentDate = lastAbsentDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public LocalDate getLastAbsentDate() {
        return lastAbsentDate;
    }

    public void setLastAbsentDate(LocalDate lastAbsentDate) {
        this.lastAbsentDate = lastAbsentDate;
    }

    @Override
    public int compareTo(Reserve o) {
        return o.lastAbsentDate.compareTo(lastAbsentDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reserve reserve = (Reserve) o;

        return visit.equals(reserve.visit);
    }

    @Override
    public int hashCode() {
        return visit.hashCode();
    }
}
