package com.example.volleybot.entity;

import javax.persistence.*;

/**
 * Created by vkondratiev on 10.09.2021
 * Description:
 */
@Entity
public class Player {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long chatId;

    @Column(nullable = false)
    private String name;

    public Player() {
    }

    public Player(Long chatId, String name) {
        this.chatId = chatId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", name='" + name + '\'' +
                '}';
    }
}
