package com.example.volleybot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by vkondratiev on 04.09.2021
 * Description:
 */
@Service
public class UpdateService {

    public BotApiMethod<?> onUpdateReceived(Update update) {
        SendMessage replyMessage = null;

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            replyMessage = new SendMessage("" + message.getChatId(), message.getText());
        }

        return replyMessage;
    }
}
