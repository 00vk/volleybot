package com.example.volleybot.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by vkondratiev on 19.09.2021
 * Description:
 */
@Entity
public class Timetable implements Comparable<Timetable>{

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private int id;

    @Column(nullable = false)
    private LocalDate gameDate;

    @Column(nullable = false)
    private int playersLimit;

    @Column(nullable = false)
    private boolean enabled;

    public Timetable(LocalDate gameDate) {
        this.gameDate = gameDate;
        this.playersLimit = 0;
        this.enabled = true;
    }

    public Timetable() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getGameDate() {
        return gameDate;
    }

    public void setGameDate(LocalDate gameDate) {
        this.gameDate = gameDate;
    }

    public int getPlayersLimit() {
        return playersLimit;
    }

    public void setPlayersLimit(int playersLimit) {
        this.playersLimit = playersLimit;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Timetable{" +
                "id=" + id +
                ", gameDate=" + gameDate +
                ", playersLimit=" + playersLimit +
                ", enabled=" + enabled +
                '}';
    }

    @Override
    public int compareTo(Timetable o) {
        return this.getGameDate().compareTo(o.gameDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Timetable timetable = (Timetable) o;

        return Objects.equals(gameDate, timetable.gameDate);
    }

    @Override
    public int hashCode() {
        return gameDate != null ? gameDate.hashCode() : 0;
    }
}
