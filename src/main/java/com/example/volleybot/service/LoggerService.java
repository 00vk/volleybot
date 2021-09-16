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
public class LoggerService {

    private static final String LOG_CHAT_ID = "-445496027";
    private final MessageSource messageSource;
    private final RestTemplate rest = new RestTemplate();

    public LoggerService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * @param logRecord сообщение из проперти messages, содержащее формат сообщения для лога.
     * @param params значения, которые должны подставиться в сообщение лога
     */
    public void logAction(final String logRecord, final String ... params) {
        Runnable runnable = () -> {
            String logFormat = messageSource.getMessage(logRecord, null, Locale.forLanguageTag("ru-RU"));
            String logText = String.format(logFormat, (Object[]) params);
            HttpEntity<Map<String, String>> logRequest;
            logRequest = new HttpEntity<>(requestBody(logText), headers());
            try {
                URI url = new URI("https://api.telegram.org/bot1983551840:AAFsUePykufh7fq_Z8CblmPkcrMvj6lzNBk/sendMessage");
                rest.postForLocation(url, logRequest);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        };
        runnable.run();
    }

    private Map<String, String> requestBody(String logText) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("chat_id", LOG_CHAT_ID);
        requestBody.put("text", logText);
        return requestBody;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("Content-Type", "application/json");
        return headers;
    }
}
