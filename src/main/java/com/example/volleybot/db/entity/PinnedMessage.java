package com.example.volleybot.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by vkondratiev on 08.10.2021
 * Description:
 */
@Entity
public class PinnedMessage {

    @Id @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "message_id")
    private Integer messageId;

    public PinnedMessage() {
    }

    public PinnedMessage(Long id, Integer messageId) {
        this.id = id;
        this.messageId = messageId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }
}
