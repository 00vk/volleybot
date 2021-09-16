//package com.example.volleybot.service;
//
//import org.springframework.context.MessageSource;
//import org.springframework.stereotype.Service;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//
//import java.util.Locale;
//
///**
// * Created by vkondratiev on 04.09.2021
// * Description:
// */
//@Service
//public class ReplyMessageService {
//
//    private final MessageSource messageSource;
//
//    public ReplyMessageService(MessageSource messageSource) {
//        this.messageSource = messageSource;
//    }
//
//    public SendMessage getSendMessage(String chatId, String replyMessage) {
//        String messageText = messageSource.getMessage(replyMessage, null, Locale.forLanguageTag("ru-RU"));
//        return new SendMessage(chatId, messageText);
//    }
//
//}
