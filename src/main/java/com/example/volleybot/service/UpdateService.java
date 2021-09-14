package com.example.volleybot.service;

import com.example.volleybot.botapi.BotStateContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by vkondratiev on 14.09.2021
 * Description:
 */

@Service
public class UpdateService {

    private final BotStateContext stateContext;

    public UpdateService(BotStateContext stateContext) {
        this.stateContext = stateContext;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
//            try {
                SendMessage outMessage = new SendMessage();
                outMessage.setChatId(String.valueOf(chatId));
                outMessage.setText("Hi! " + update.getMessage().getText());
//                execute(outMessage);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
        }
        return outMessage;
    }
}
