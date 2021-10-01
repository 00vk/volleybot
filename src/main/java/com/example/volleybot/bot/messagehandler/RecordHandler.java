package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.service.SendMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by vkondratiev on 27.09.2021
 * Description:
 */
@Component
public class RecordHandler implements IUpdateHandler{

    private final SendMessageService messageService;
    private final PlayerCache playerCache;

    public RecordHandler(SendMessageService messageService, PlayerCache playerCache) {
        this.messageService = messageService;
        this.playerCache = playerCache;
    }

    @Override
    public void handle(Update update) {


    }

    public void handleRecord(long adminId) {

    }

    @Override
    public BotState state() {
        return null;
    }
}
