package com.example.volleybot.bot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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

    private final RestTemplate rest = new RestTemplate();
    @Value("${telegrambot.token}")
    private String TOKEN;
    @Value("${telegrambot.log-chat-id}")
    private long LOG_CHAT_ID;
    @Value("${telegrambot.target-chat-id}")
    private long TARGET_CHAT_ID;
    private final Map<Long, Integer> messageIds = new HashMap<>();
    private Integer pinnedMessageId;

    public void sendMessage(long chatId, InlineKeyboardMarkup keyboard, String msgText) {
        Runnable runnable = () -> {
            HttpEntity<Map<String, Object>> sendMessageRequest;
            sendMessageRequest = new HttpEntity<>(sendMessageRequest(chatId, msgText, keyboard), headers());
            try {
                URI url = new URI("https://api.telegram.org/bot" + TOKEN + "/sendMessage");
                Map<String, Object> message = rest.postForObject(url, sendMessageRequest, Map.class);
                int messageId = (Integer) ((Map<String, Object>) message.get("result")).get("message_id");
                messageIds.put(chatId, messageId);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        };
        runnable.run();
    }

    public void log(String msgText) {
        sendMessage(LOG_CHAT_ID, null, msgText);
    }

    public void logMock() {
        StackTraceElement invoker = Thread.currentThread().getStackTrace()[2];
        sendMessage(LOG_CHAT_ID, null, invoker.getClassName() + "#" + invoker.getMethodName() + "(): не реализовано");
    }

    public void editKeyboard(long chatId, InlineKeyboardMarkup keyboard) {
        Runnable runnable = () -> {
            HttpEntity<Map<String, Object>> editRequest;
            editRequest = new HttpEntity<>(editMessageRequest(chatId, messageIds.get(chatId), keyboard), headers());
            try {
                URI url = new URI("https://api.telegram.org/bot" + TOKEN + "/editMessageReplyMarkup");
                rest.postForLocation(url, editRequest);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        };
        runnable.run();
    }

    public void nextVisitMessage(final String text) {
        Runnable runnable = () -> {
            unpinPreviousVisitMessage();
            sendNextVisitMessage(text);
            pinNextVisitMessage();
        };
        runnable.run();
    }

    public void editPinnedMessage(String text) {
        if (pinnedMessageId == null)
            return;

        Runnable runnable = () -> {
            HttpEntity<Map<String, Object>> editRequest;
            editRequest = new HttpEntity<>(editMessageTextRequest(TARGET_CHAT_ID, pinnedMessageId, text), headers());
            try {
                URI url = new URI("https://api.telegram.org/bot" + TOKEN + "/editMessageText");
                rest.postForLocation(url, editRequest);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        };
        runnable.run();
    }

    private void unpinPreviousVisitMessage() {
        if (pinnedMessageId == null)
            return;

        HttpEntity<Map<String, Object>> editRequest;
        editRequest = new HttpEntity<>(editMessageRequest(TARGET_CHAT_ID, pinnedMessageId, null), headers());
        try {
            URI url = new URI("https://api.telegram.org/bot" + TOKEN + "/unpinChatMessage");
            rest.postForLocation(url, editRequest);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void sendNextVisitMessage(String text) {
        HttpEntity<Map<String, Object>> sendMessageRequest;
        sendMessageRequest = new HttpEntity<>(sendMessageRequest(TARGET_CHAT_ID, text, null), headers());
        try {
            URI url = new URI("https://api.telegram.org/bot" + TOKEN + "/sendMessage");
            Map<String, Object> message = rest.postForObject(url, sendMessageRequest, Map.class);
            pinnedMessageId = (Integer) ((Map<String, Object>) message.get("result")).get("message_id");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void pinNextVisitMessage() {
        HttpEntity<Map<String, Object>> editRequest;
        editRequest = new HttpEntity<>(editMessageRequest(TARGET_CHAT_ID, pinnedMessageId, null), headers());
        try {
            URI url = new URI("https://api.telegram.org/bot" + TOKEN + "/pinChatMessage");
            rest.postForLocation(url, editRequest);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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

    private Map<String, Object> editMessageRequest(long chatId, long messageId, InlineKeyboardMarkup keyboard) {
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

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("Content-Type", "application/json");
        return headers;
    }
}
