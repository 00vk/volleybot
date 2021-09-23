package com.example.volleybot.bot;

import com.example.volleybot.bot.messagehandler.IUpdateHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vkondratiev on 14.09.2021
 * Description:
 */
@Component
public class BotStateContext {

    private final Map<BotState, IUpdateHandler> handlers = new HashMap<>();

    public BotStateContext(List<IUpdateHandler> handlers) {
        handlers.forEach(handler -> this.handlers.put(handler.state(), handler));
        for (BotState state : BotState.values()) {
            if (!this.handlers.containsKey(state))
                throw new IllegalStateException("Не определен хэндлер для состояния " + state);
        }
    }

    public IUpdateHandler handler(BotState state) {
        return handlers.get(state);
    }
}
