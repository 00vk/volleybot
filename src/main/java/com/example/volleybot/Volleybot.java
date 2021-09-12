package com.example.volleybot;

import com.example.volleybot.config.BotConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Created by vkondratiev on 04.09.2021
 * Description:
 */

@EnableConfigurationProperties(BotConfig.class)
public class Volleybot extends TelegramWebhookBot {

    private final String username;
    private final String webhookPath;
    private final String botToken;


    public Volleybot(String username, String webhookPath, String botToken) {
        this.username = username;
        this.webhookPath = webhookPath;
        this.botToken = botToken;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
//        return updateService.onUpdateReceived(update);
        if (update.getMessage() != null && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            try {
                SendMessage outMessage = new SendMessage();
                outMessage.setChatId(String.valueOf(chatId));
                outMessage.setText("Hi! " + update.getMessage().getText());
                execute(outMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //    public void botConnect() {
//        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
//        try {
//            telegramBotsApi.registerBot(this);
//            log.info("TelegramAPI started. Look for messages");
//        } catch (TelegramApiRequestException e) {
//            log.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
//            try {
//                Thread.sleep(RECONNECT_PAUSE);
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//                return;
//            }
//            botConnect();
//        }
//    }
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
        return botToken;
    }
}
