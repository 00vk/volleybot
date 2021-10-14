package com.example.volleybot.bot.service;

import com.example.volleybot.bot.cache.PinnedMessagesCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
@Service
public class SendMessageService {

    public static final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate rest = new RestTemplate();
    private final PinnedMessagesCache messageIds;
    @Value("${telegrambot.token}")
    private String TOKEN;
    @Value("${telegrambot.target-chat-id}")
    private long TARGET_CHAT_ID;
    @Value("${telegrambot.self-id}")
    private long SELF_ID;
    @Value("${telegrambot.owner-id}")
    private long OWNER_ID;

    public SendMessageService(PinnedMessagesCache messageIds) {
        this.messageIds = messageIds;
    }

    public void sendMessage(long chatId, InlineKeyboardMarkup keyboard, String msgText) {
        Runnable runnable = () -> {
            HttpEntity<Map<String, Object>> sendMessageRequest;
            sendMessageRequest = new HttpEntity<>(sendMessageRequest(chatId, msgText, keyboard), headers());
            URI url = getUrl("sendMessage");
            rest.postForLocation(url, sendMessageRequest);
        };
        runnable.run();
    }

    public void sendUpdatableMessage(long chatId, InlineKeyboardMarkup keyboard, String msgText) {
        Runnable runnable = () -> {
            HttpEntity<Map<String, Object>> sendMessageRequest;
            sendMessageRequest = new HttpEntity<>(sendMessageRequest(chatId, msgText, keyboard), headers());
            URI url = getUrl("sendMessage");
            Map<String, Object> response = rest.postForObject(url, sendMessageRequest, Map.class);
            Message message = mapper.convertValue(response.get("result"), Message.class);
            if (message == null) return;
            messageIds.save(chatId, message.getMessageId());
        };
        runnable.run();
    }

    public void log(String msgText) {
//        sendMessage(LOG_CHAT_ID, null, msgText);
    }

    public void editKeyboard(long chatId, InlineKeyboardMarkup keyboard) {
        Integer savedMessageId = messageIds.savedId(chatId);
        if (savedMessageId == null) {
            return;
        }

        Runnable runnable = () -> {
            HttpEntity<Map<String, Object>> editRequest;
            editRequest = new HttpEntity<>(editMessageRequest(chatId, savedMessageId, keyboard), headers());
            URI url = getUrl("editMessageReplyMarkup");
            rest.postForLocation(url, editRequest);
        };
        runnable.run();
    }

    public void deleteMessage(long chatId) {
        Integer savedMessageId = messageIds.savedId(chatId);
        if (savedMessageId == null) {
            return;
        }
        messageIds.forget(chatId);

        Runnable runnable = () -> {
            HttpEntity<Map<String, Object>> editRequest;
            editRequest = new HttpEntity<>(editMessageRequest(chatId, savedMessageId, null), headers());
            URI url = getUrl("deleteMessage");
            rest.postForLocation(url, editRequest);
        };
        runnable.run();
    }

    public void updateListMessage(final String text) {
        Runnable runnable = () -> {
            Message pinnedMessage = getPinnedMessage();
            if (pinnedMessage != null && pinnedMessage.getFrom().getId() == SELF_ID) {
                editMessageText(TARGET_CHAT_ID, pinnedMessage.getMessageId(), text);
            } else {
                Message message = nextVisitMessageForTarget(text);
                if (message == null) return;
                pinMessage(message);
            }
        };
        runnable.run();
    }

    public void editMessageText(Long chatId, Integer messageId, String text) {
        Runnable runnable = () -> {
            HttpEntity<Map<String, Object>> editRequest;
            editRequest = new HttpEntity<>(editMessageTextRequest(chatId, messageId, text), headers());
            try {
                URI url = getUrl("editMessageText");
                rest.postForLocation(url, editRequest);
            } catch (HttpClientErrorException e) {
                if (e.getMessage() == null) return;
                sendMessage(OWNER_ID, null, e.getMessage());
            }
        };
        runnable.run();
    }

    public void nextVisitMessage(String text) {
        Runnable runnable = () -> {
            unpinPreviousVisitMessage();
            sendNextVisitMessage(text);
            pinNextVisitMessage();
        };
        runnable.run();
    }

    private void unpinPreviousVisitMessage() {
        Integer pinnedMessageId = messageIds.savedId(TARGET_CHAT_ID);
        if (pinnedMessageId == null) {
            return;
        }

        HttpEntity<Map<String, Object>> editRequest;
        editRequest = new HttpEntity<>(editMessageRequest(TARGET_CHAT_ID, null, null), headers());
        URI url = getUrl("unpinChatMessage");
        rest.postForLocation(url, editRequest);
    }

    private void sendNextVisitMessage(String text) {
        Message message = nextVisitMessageForTarget(text);
        if (message == null) return;
        messageIds.save(TARGET_CHAT_ID, message.getMessageId());
    }

    private void pinNextVisitMessage() {
        Integer pinnedMessageId = messageIds.savedId(TARGET_CHAT_ID);
        if (pinnedMessageId == null) {
            return;
        }
        HttpEntity<Map<String, Object>> editRequest;
        editRequest = new HttpEntity<>(editMessageRequest(TARGET_CHAT_ID, pinnedMessageId, null), headers());
        URI url = getUrl("pinChatMessage");
        rest.postForLocation(url, editRequest);
    }

    private Map<String, Object> sendMessageRequest(long chatId, String text, InlineKeyboardMarkup keyboard) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("chat_id", chatId);
        requestBody.put("text", text);
        if (keyboard != null) {
            requestBody.put("reply_markup", keyboard);
        }
        return requestBody;
    }

    private Map<String, Object> editMessageRequest(Long chatId, Integer messageId, InlineKeyboardMarkup keyboard) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("chat_id", chatId);
        requestBody.put("message_id", messageId);
        if (keyboard != null) {
            requestBody.put("reply_markup", keyboard);
        }
        return requestBody;
    }

    private Map<String, Object> editMessageTextRequest(long chatId, int messageId, String text) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("chat_id", chatId);
        requestBody.put("message_id", messageId);
        requestBody.put("text", text);
        return requestBody;
    }

    private Message getPinnedMessage() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("chat_id", TARGET_CHAT_ID);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers());
        URI url = getUrl("getChat");
        Map response = rest.postForObject(url, request, Map.class);
        Chat chat = mapper.convertValue(response.get("result"), Chat.class);
        if (chat == null) return null;
        return chat.getPinnedMessage();
    }

    private Message nextVisitMessageForTarget(String text) {
        HttpEntity<Map<String, Object>> sendMessageRequest;
        sendMessageRequest = new HttpEntity<>(sendMessageRequest(TARGET_CHAT_ID, text, null), headers());
        URI url1 = getUrl("sendMessage");
        Map response = rest.postForObject(url1, sendMessageRequest, Map.class);
        return mapper.convertValue(response.get("result"), Message.class);
    }

    private void pinMessage(Message message) {
        HttpEntity<Map<String, Object>> editRequest;
        editRequest = new HttpEntity<>(editMessageRequest(TARGET_CHAT_ID, message.getMessageId(), null), headers());
        URI url2 = getUrl("pinChatMessage");
        rest.postForLocation(url2, editRequest);
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("Content-Type", "application/json");
        return headers;
    }

    private URI getUrl(String s) {
        try {
            return new URI("https://api.telegram.org/bot%s/%s".formatted(TOKEN, s));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
