package com.example.volleybot.db.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by vkondratiev on 10.09.2021
 * Description:
 */
@Entity
public class Player {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    @Column(nullable = false)
    private boolean isAdmin;

    @OneToMany(targetEntity = Visit.class, mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Visit> visits = new LinkedHashSet<>();

    public Player() {
    }

    public Player(long chatId) {
        this.id = chatId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Set<Visit> getVisits() {
        return visits;
    }

    public void setVisits(Set<Visit> visits) {
        this.visits = visits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return id.equals(player.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
