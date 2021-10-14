package com.example.volleybot.bot;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
public enum BotState {

    START, // бот запущен, требуется авторизация
    AUTH, // авторизация, нужен пароль
    NAME, // пароль введен, надо ввести имя
    MAIN, // список команд
    VISIT, // запись, отмена записи
    LIST, // список записавшихся
    RECORD,
    ;

    public static BotState of(String callbackData) {
        String command = callbackData.split(" ")[0];
        return switch (command) {
            case "/list" -> BotState.LIST;
            case "/visit" -> BotState.VISIT;
            case "/record" -> BotState.RECORD;
            default -> MAIN;
        };
    }
}
