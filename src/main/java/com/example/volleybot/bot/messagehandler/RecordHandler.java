package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.service.InlineKeyboardService;
import com.example.volleybot.bot.service.SendMessageService;
import com.example.volleybot.bot.service.VisitManager;
import com.example.volleybot.db.entity.Player;
import com.example.volleybot.db.service.PlayerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by vkondratiev on 27.09.2021
 * Description:
 */
@Component
public class RecordHandler implements IUpdateHandler {

    private final SendMessageService messageService;
    private final InlineKeyboardService keyboardService;
    private final PlayerService playerService;
    private final PlayerCache playerCache;
    private final VisitManager visitManager;
    private final Map<Long, Long> playerIds = new HashMap<>();

    public RecordHandler(SendMessageService messageService,
                         InlineKeyboardService keyboardService,
                         PlayerService playerService,
                         PlayerCache playerCache,
                         VisitManager visitManager) {
        this.messageService = messageService;
        this.playerCache = playerCache;
        this.playerService = playerService;
        this.keyboardService = keyboardService;
        this.visitManager = visitManager;
    }

    @Override
    public boolean handle(Message message) {
        long userId = message.getFrom().getId();
        if (!playerCache.isPlayerAdmin(userId) || !message.hasText())
            return true;
        String messageText = message.getText();
        if ("/record".equals(messageText)) {
            messageService.sendUpdatableMessage(userId, choosePlayerKeyboard(1), choosePlayerMessage());
            messageService.log("%s выбирает игрока для записи".formatted(playerCache.getPlayerName(userId)));
            return true;
        }
        playerCache.setUserBotState(userId, BotState.of(messageText));
        return false;
    }

    @Override
    public boolean handle(CallbackQuery callback) {
        long userId = callback.getFrom().getId();
        if (!playerCache.isPlayerAdmin(userId))
            return false;
        String data = callback.getData();
        String[] commands = data.split(" ");
        if ("/main".equals(commands[0])) {
            playerCache.setUserBotState(userId, BotState.MAIN);
            return false;
        } else if ("page".equals(commands[1])) {
            changePage(userId, commands);
        } else if ("player".equals(commands[1])) {
            long playerId = Long.parseLong(commands[2]);
            InlineKeyboardMarkup keyboard = visitManager.visitKeyboard("/record", playerId);
            messageService.sendUpdatableMessage(userId, keyboard, chooseDateMessage(commands));
            this.playerIds.put(userId, playerId);
        } else if ("date".equals(commands[1])) {
            visitManager.manage(userId, playerIds.get(userId), "/record", commands[2]);
        }
        return true;
    }

    @Override
    public BotState state() {
        return BotState.RECORD;
    }

    private void changePage(long userId, String[] commands) {
        int currentPage = Integer.parseInt(commands[2]);
        int targetPage = Integer.parseInt(commands[3]);
        int pagesTotal = Integer.parseInt(commands[4]);
        if (targetPage != currentPage && targetPage > 0 && targetPage <= pagesTotal) {
            messageService.editKeyboard(userId, choosePlayerKeyboard(targetPage));
        }
    }

    private InlineKeyboardMarkup choosePlayerKeyboard(int pageNum) {
        List<Player> players = playerService.getAllPlayers();
        List<String> names = players.stream().map(Player::getName)
                                    .sorted().collect(Collectors.toList());
        List<String> callbackData = players.stream().map(Player::getId)
                                           .map(id -> "/record player " + id)
                                           .collect(Collectors.toList());
        return keyboardService.createKeyboard(1, names, callbackData, pageNum);
    }

    private String choosePlayerMessage() {
        return "Выбери, кого требуется записать:";
    }

    private String chooseDateMessage(String[] commands) {
        String name = "";
        for (int i = 2; i < commands.length; i++) {
            name = String.join(" ", name, commands[i]);
        }
        return "Записываем игрока: %s\nВыбери дату:".formatted(name);
    }
}
