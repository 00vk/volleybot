package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
@Component
public interface IUpdateHandler {

    default boolean handle(Update update){
        if (update.hasMessage())
            return handle(update.getMessage());
        if (update.hasCallbackQuery())
            return handle(update.getCallbackQuery());
        return true;
    }

    default boolean handle(Message message) {
        return true;
    }

    default boolean handle(CallbackQuery callback) {
        return true;
    }

    BotState state();
}
