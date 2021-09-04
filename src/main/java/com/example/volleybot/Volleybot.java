package com.example.volleybot;

import com.example.volleybot.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by vkondratiev on 04.09.2021
 * Description:
 */
public class Volleybot extends TelegramWebhookBot {

    public Volleybot(String userName, String webHookPath, String botToken) {

    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
//        return updateService.onUpdateReceived(update);
        SendMessage replyMessage = null;

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            replyMessage = new SendMessage("" + message.getChatId(), message.getText());
        }

        return replyMessage;
    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return null;
    }

    @Override
    public String getBotPath() {
        return null;
    }
}
