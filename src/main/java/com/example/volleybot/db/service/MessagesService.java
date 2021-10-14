package com.example.volleybot.db.service;

import com.example.volleybot.db.entity.PinnedMessage;
import com.example.volleybot.db.repository.MessagesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by vkondratiev on 08.10.2021
 * Description:
 */
@Service
public class MessagesService {

    private final MessagesRepository repository;
    @Value("${telegrambot.target-chat-id}")
    private Long TARGET_CHAT_ID;

    public MessagesService(MessagesRepository repository) {
        this.repository = repository;
    }

    public void pinMessage(Long chatId, Integer messageId) {
        PinnedMessage message = new PinnedMessage(chatId, messageId);
        repository.save(message);
    }

    public Integer getTargetChatPinnedId() {
        PinnedMessage pinnedMessage = repository.findById(TARGET_CHAT_ID)
                                                .orElse(null);
        if (pinnedMessage == null)
            return null;
        return pinnedMessage.getMessageId();
    }

    public List<PinnedMessage> getAll() {
        return repository.findAll();
    }

    public void unpinMessage(PinnedMessage message) {
        repository.delete(message);
    }

    public void pinTargetChatMessage(Integer pinnedMessageId) {
        pinMessage(TARGET_CHAT_ID, pinnedMessageId);
    }
}
