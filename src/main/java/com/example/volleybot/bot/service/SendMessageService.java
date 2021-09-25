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
    // TODO: TARGET_CHAT_ID
    private final Map<Long, Integer> messageIds = new HashMap<>();

    public void sendMessage(long chatId, InlineKeyboardMarkup keyboard, String msgText) {
        Runnable runnable = () -> {
            HttpEntity<Map<String, Object>> replyRequest;
            replyRequest = new HttpEntity<>(sendMessageRequest(chatId, msgText, keyboard), headers());
            try {
                URI url = new URI("https://api.telegram.org/bot" + TOKEN + "/sendMessage");
                Map<String, Object> message = rest.postForObject(url, replyRequest, Map.class);
                int messageId = (Integer) ((Map<String, Object>) message.get("result")).get("message_id");
                messageIds.put(chatId, messageId);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        };
        runnable.run();
    }

    public void editKeyboard(long chatId, InlineKeyboardMarkup keyboard) {
        Runnable runnable = () -> {
            HttpEntity<Map<String, Object>> replyRequest;
            replyRequest = new HttpEntity<>(editKeyboardRequest(chatId, messageIds.get(chatId), keyboard), headers());
            try {
                URI url = new URI("https://api.telegram.org/bot" + TOKEN + "/editMessageReplyMarkup");
                rest.postForLocation(url, replyRequest);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        };
        runnable.run();
    }

    public void log(String msgText) {
        sendMessage(LOG_CHAT_ID, null, msgText);
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

    private Map<String, Object> editKeyboardRequest(long chatId, long messageId, InlineKeyboardMarkup keyboard) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("chat_id", chatId);
        requestBody.put("message_id", messageId);
        if (keyboard != null) {
            requestBody.put("reply_markup", keyboard);
        }
        return requestBody;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("Content-Type", "application/json");
        return headers;
    }

    public void logMock() {
        StackTraceElement invoker = Thread.currentThread().getStackTrace()[2];
        sendMessage(LOG_CHAT_ID, null, invoker.getClassName() + "#" + invoker.getMethodName() + "(): не реализовано");
    }
}
