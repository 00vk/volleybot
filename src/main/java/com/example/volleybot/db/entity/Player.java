package com.example.volleybot.db.entity;

import javax.persistence.*;

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

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
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
