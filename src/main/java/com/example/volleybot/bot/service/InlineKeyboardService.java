package com.example.volleybot.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vkondratiev on 20.09.2021
 * Description:
 */
@Service
public class InlineKeyboardService {

    public InlineKeyboardMarkup createKeyboard(int columns, List<String> buttonTexts) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (int i = 0; i < buttonTexts.size(); i++) {
            if (i % columns == 0) {
                buttons.add(new ArrayList<>());
            }
            List<InlineKeyboardButton> row = buttons.get(buttons.size() - 1);
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonTexts.get(i));
            button.setCallbackData(buttonTexts.get(i));
            row.add(button);
        }
        return new InlineKeyboardMarkup(buttons);
    }
}
