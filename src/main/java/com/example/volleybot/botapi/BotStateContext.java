package com.example.volleybot.botapi;

import com.example.volleybot.botapi.messagehandler.IMessageHandler;
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

    private final Map<BotState, IMessageHandler> handlers = new HashMap<>();

    public BotStateContext(List<IMessageHandler> handlers) {
        handlers.forEach(handler -> this.handlers.put(handler.state(), handler));
        for (BotState state : BotState.values()) {
            if (!this.handlers.containsKey(state))
                throw new IllegalStateException("Не определен хэндлер для состояния " + state);
        }
    }

    public IMessageHandler handler(BotState state) {
        return handlers.get(state);
    }
}
