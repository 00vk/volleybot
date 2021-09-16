package com.example.volleybot.botapi.messagehandler;

import com.example.volleybot.botapi.BotState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
@Component
public interface IMessageHandler {

    void handle(Message inMessage);

    BotState state();
}
