package com.example.volleybot.service;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
@Service
public class SendMessageService {

    private static final long LOG_CHAT_ID = -445496027L;
    private final MessageSource messageSource;
    private final RestTemplate rest = new RestTemplate();
    private String token;

    public SendMessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * @param chatId   идентификатор чата
     * @param replyTag сообщение из проперти messages, содержащее формат сообщения для лога.
     * @param params   значения, которые должны подставиться в сообщение
     */
    public void sendMessage(long chatId, String replyTag, String... params) {
        Runnable runnable = () -> {
            String replyFormat = messageSource.getMessage(replyTag, null, Locale.forLanguageTag("ru-RU"));
            String replyText = String.format(replyFormat, (Object[]) params);
            HttpEntity<Map<String, Object>> replyRequest;
            replyRequest = new HttpEntity<>(requestBody(chatId, replyText), headers());
            try {
                URI url = new URI("https://api.telegram.org/bot" + token + "/sendMessage");
                rest.postForLocation(url, replyRequest);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        };
        runnable.run();
    }

    public void log(String logRecord, String... params) {
        sendMessage(LOG_CHAT_ID, logRecord, params);
    }

    private Map<String, Object> requestBody(long chatId, String logText) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("chat_id", chatId);
        requestBody.put("text", logText);
        return requestBody;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("Content-Type", "application/json");
        return headers;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
