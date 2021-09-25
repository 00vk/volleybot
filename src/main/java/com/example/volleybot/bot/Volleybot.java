package com.example.volleybot.bot;

import com.example.volleybot.bot.config.BotConfig;
import com.example.volleybot.bot.manager.TimetableManager;
import com.example.volleybot.bot.service.UpdateService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;

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
    private final TimetableManager timetableManager;

    public Volleybot(String username,
                     String webhookPath,
                     String token,
                     UpdateService service,
                     TimetableManager timetableManager) {
        this.username = username;
        this.webhookPath = webhookPath;
        this.token = token;
        this.updateService = service;
        this.timetableManager = timetableManager;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        updateService.handleUpdate(update);
        return null;
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

    @PostConstruct
    private void manageTimetable() {
        timetableManager.manageDates();
    }
}
