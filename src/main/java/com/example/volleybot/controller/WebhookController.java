package com.example.volleybot.controller;

import com.example.volleybot.Volleybot;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by vkondratiev on 04.09.2021
 * Description:
 */
@RestController
public class WebhookController {

    private final Volleybot volleybot;

    public WebhookController(Volleybot volleybot) {
        this.volleybot = volleybot;
    }

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return volleybot.onWebhookUpdateReceived(update);
    }
}
