package com.example.volleybot.config;

import com.example.volleybot.Volleybot;
import com.example.volleybot.service.UpdateService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by vkondratiev on 04.09.2021
 * Description:
 */

@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {

    private String webHookPath;
    private String userName;
    private String botToken;

    @Bean
    public Volleybot volleybot() {
        return new Volleybot(userName, webHookPath, botToken);
    }

    public String getWebHookPath() {
        return webHookPath;
    }

    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
}
