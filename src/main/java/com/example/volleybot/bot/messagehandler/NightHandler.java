package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.service.SendMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by vkondratiev on 26.09.2021
 * Description:
 */
@Component
public class NightHandler implements IUpdateHandler{

    private final SendMessageService messageService;

    public NightHandler(SendMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage())
            return;
        Long fromId = update.getMessage().getFrom().getId();
        messageService.sendVoice(fromId);
    }

    @Override
    public BotState state() {
        return null;
    }
}
