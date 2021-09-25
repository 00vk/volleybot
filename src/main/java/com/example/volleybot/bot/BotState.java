package com.example.volleybot.bot;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
public enum BotState {

    START, // бот запущен. требуется авторизация
    AUTH, // авторизация, нужен пароль
    NAME, // пароль введен, надо ввести имя
    MAIN, // список команд
    VISIT // запись, отмена записи
}
