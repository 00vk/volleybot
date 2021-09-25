package com.example.volleybot.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

/**
 * Created by vkondratiev on 23.09.2021
 * Description:
 */
@Entity
public class Visit {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(targetEntity = Timetable.class)
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;

    @ManyToOne(targetEntity = Player.class)
    @JoinColumn(name = "player_id")
    private Player player;

    private boolean isActive;

    public Visit() {
    }

    public Visit(Player player, Timetable timetable) {
        this.player = player;
        this.timetable = timetable;
        this.isActive = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    public void setTimetable(Timetable timetable) {
        this.timetable = timetable;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player players) {
        this.player = players;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Visit visit = (Visit) o;

        if (!Objects.equals(timetable, visit.timetable)) return false;
        return Objects.equals(player, visit.player);
    }

    @Override
    public int hashCode() {
        int result = timetable != null ? timetable.hashCode() : 0;
        result = 31 * result + (player != null ? player.hashCode() : 0);
        return result;
    }
}
