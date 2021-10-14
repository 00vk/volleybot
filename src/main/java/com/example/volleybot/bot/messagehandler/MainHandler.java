package com.example.volleybot.bot.messagehandler;

import com.example.volleybot.bot.BotState;
import com.example.volleybot.bot.cache.PlayerCache;
import com.example.volleybot.bot.service.SendMessageService;
import com.example.volleybot.bot.service.TimetableManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Created by vkondratiev on 19.09.2021
 * Description:
 */
@Component
public class MainHandler implements IUpdateHandler {

    private final TimetableManager timetableManager;
    private final PlayerCache playerCache;
    private final SendMessageService messageService;

    public MainHandler(TimetableManager timetableManager,
                       PlayerCache playerCache,
                       SendMessageService messageService) {
        this.timetableManager = timetableManager;
        this.playerCache = playerCache;
        this.messageService = messageService;
    }

    @Override
    public boolean handle(Message message) {
        Long userId = message.getFrom().getId();
        boolean isAdmin = playerCache.isPlayerAdmin(userId);
        String messageText = message.getText();
        String[] split = messageText.split("[ @]");
        String command = split[0];
        if (isAdmin && "/dates".equals(command)) {
            timetableManager.manageDates();
        } else if (isAdmin && "/pin".equals(command)) {
            timetableManager.managePinnedMessage();
            playerCache.setUserBotState(userId, BotState.VISIT);
        } else {
            BotState state = BotState.of(command);
            if (state != BotState.MAIN) {
                playerCache.setUserBotState(userId, state);
                return false;
            }
        }
        sendMainMessage(userId);
        return true;
    }

    @Override
    public boolean handle(CallbackQuery callback) {
        Long userId = callback.getFrom().getId();
        sendMainMessage(userId);
        return true;
    }

    @Override
    public BotState state() {
        return BotState.MAIN;
    }

    void sendMainMessage(Long id) {
        boolean isAdmin = playerCache.isPlayerAdmin(id);
        messageService.sendMessage(id, null, getMainMessage(isAdmin));
    }

    private String getMainMessage(boolean isAdmin) {
        String msgText = """
                Доступные команды:
                📝 /visit - записаться на игру / отменить запись
                📃 /list - посмотреть список участников
                """;
        if (isAdmin) {
            msgText += """
                    🙋🏻 /record - записать игрока
                    ✌️ /addCourt - добавить площадку (лимит +12)
                    🙅🏻‍♂️ /removeCourt - убрать площадку / отменить игру (лимит -12)
                    📌 /pin - обновить закрепленное сообщение
                    🗓️ /dates - произвести настройку дат
                    ✍️ /rename - переименовать игрока
                    ❔ /state - проверить состояние бота для участника
                    """;
        }
        return msgText;
    }
}
