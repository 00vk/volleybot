package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
@Component
public interface IUpdateHandler {

    void handle(Update update);

    BotState state();
}
