package com.example.volleybot.bot.cache;

import com.example.volleybot.db.entity.PinnedMessage;
import com.example.volleybot.db.service.MessagesService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vkondratiev on 08.10.2021
 * Description:
 */
@Component
public class PinnedMessagesCache {

    private final MessagesService service;
    private final Map<Long, PinnedMessage> messageIds = new HashMap<>();

    public PinnedMessagesCache(MessagesService service) {
        this.service = service;
        service.getAll().forEach(m -> messageIds.put(m.getId(), m));
    }

    public Integer savedId(Long chatId) {
        PinnedMessage pinnedMessage = messageIds.get(chatId);
        if (pinnedMessage == null)
            return null;
        return pinnedMessage.getMessageId();
    }

    public void save(Long chatId, Integer messageId) {
        if (messageId == null) {
            forget(chatId);
            return;
        }
        messageIds.put(chatId, new PinnedMessage(chatId, messageId));
        service.pinTargetChatMessage(messageId);
    }

    public void forget(Long chatId) {
        PinnedMessage message = messageIds.remove(chatId);
        service.unpinMessage(message);
    }
}
