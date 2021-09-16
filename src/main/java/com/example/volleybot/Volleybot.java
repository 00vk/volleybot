package com.example.volleybot;

import com.example.volleybot.config.BotConfig;
import com.example.volleybot.service.UpdateService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Created by vkondratiev on 04.09.2021
 * Description:
 */

@EnableConfigurationProperties(BotConfig.class)
public class Volleybot extends TelegramWebhookBot {

    private final String webhookPath;
    private final String username;
    private final String token;
    private final UpdateService updateService;

    public Volleybot(String username, String webhookPath, String token, UpdateService service) {
        this.username = username;
        this.webhookPath = webhookPath;
        this.token = token;
        this.updateService = service;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return updateService.handleUpdate(update);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotPath() {
        return webhookPath;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
