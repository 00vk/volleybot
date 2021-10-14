package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.cache.TimetableCache;
import com.example.volleybot.bot.cache.VisitCache;
import com.example.volleybot.bot.service.InlineKeyboardService;
import com.example.volleybot.bot.service.SendMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by vkondratiev on 05.10.2021
 * Description:
 */
@Component
public class ListHandler implements IUpdateHandler {

    private final SendMessageService messageService;
    private final PlayerCache playerCache;
    private final TimetableCache timetableCache;
    private final InlineKeyboardService keyboardService;
    private final VisitCache visitCache;

    public ListHandler(SendMessageService messageService,
                       PlayerCache playerCache,
                       TimetableCache timetableCache,
                       InlineKeyboardService keyboardService,
                       VisitCache visitCache) {
        this.messageService = messageService;
        this.playerCache = playerCache;
        this.timetableCache = timetableCache;
        this.keyboardService = keyboardService;
        this.visitCache = visitCache;
    }

    @Override
    public boolean handle(Message message) {
        long userId = message.getFrom().getId();
        if (!message.hasText())
            return true;
        String messageText = message.getText();
        if ("/list".equals(messageText)) {
            messageService.sendMessage(userId, getKeyboard(), "\uD83D\uDCC5 Выбери дату: ");
            return true;
        }
        playerCache.setUserBotState(userId, BotState.of(messageText));
        return false;
    }

    @Override
    public boolean handle(CallbackQuery callback) {
        long userId = callback.getFrom().getId();
        String[] commands = callback.getData().split(" ");
        if (!"/main".equals(commands[0])) {
            LocalDate date = timetableCache.parse(commands[1]);
            String msgText = visitCache.listOfPlayers(date);
            messageService.sendMessage(userId, null, msgText);
        }
        playerCache.setUserBotState(userId, BotState.MAIN);
        return false;
    }

    @Override
    public BotState state() {
        return BotState.LIST;
    }

    private InlineKeyboardMarkup getKeyboard() {
        List<String> texts = new ArrayList<>();
        List<String> callbackData = new ArrayList<>();
        Set<LocalDate> forwardDates = timetableCache.getDatesAfter(LocalDate.now());
        for (LocalDate date : forwardDates) {
            texts.add(timetableCache.toText(date));
            callbackData.add("/list " + timetableCache.format(date));
        }
        return keyboardService.createKeyboard(4, texts, callbackData);
    }
}
