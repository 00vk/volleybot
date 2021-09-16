package com.example.volleybot.config;

import com.example.volleybot.Volleybot;
import com.example.volleybot.service.UpdateService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Created by vkondratiev on 04.09.2021
 * Description:
 */

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {

    private String webhookPath;
    private String username;
    private String token;

    @Bean
    public Volleybot volleybot(UpdateService service) {
        service.setToken(token);
        return new Volleybot(username, webhookPath, token, service);
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setDefaultEncoding("UTF-8");
        source.setBasename("classpath:messages");
        return source;
    }

    public String getWebhookPath() {
        return webhookPath;
    }

    public void setWebhookPath(String webhookPath) {
        this.webhookPath = webhookPath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
