package com.example.volleybot.bot;

/**
 * Created by vkondratiev on 16.09.2021
 * Description:
 */
public enum BotState {

    START, // бот запущен. требуется авторизация
    AUTH, // авторизация, нужен пароль
    AUTH_SUCCESS, // пароль введен, надо ввести имя
    MAIN, // список команд
}
