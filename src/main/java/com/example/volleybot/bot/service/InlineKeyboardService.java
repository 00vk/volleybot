package com.example.volleybot.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vkondratiev on 20.09.2021
 * Description:
 */
@Service
public class InlineKeyboardService {

    public InlineKeyboardMarkup createKeyboard(int columns, List<String> buttonTexts, List<String> callbackData) {
        return createKeyboard(columns, buttonTexts, callbackData, 1);
    }

    public InlineKeyboardMarkup createKeyboard(int columns, List<String> buttonTexts, List<String> callbackData, int pageNum) {
        int buttonsLimit = 8 * columns;
        String handlerName = callbackData.get(0).split(" ")[0];
        List<List<List<InlineKeyboardButton>>> keyboards = new ArrayList<>();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < buttonTexts.size(); i++) {
            row.add(buttonOf(buttonTexts.get(i), callbackData.get(i)));
            if (row.size() == columns || i == buttonTexts.size() - 1) {
                buttons.add(row);
                row = new ArrayList<>();
            }
            if ((i + 1) % buttonsLimit == 0 || i == buttonTexts.size() - 1) {
                if (buttonsLimit < buttonTexts.size()) {
                    row = pageButtonsRow(handlerName, i, columns, buttonTexts.size());
                    buttons.add(row);
                    row = new ArrayList<>();
                }
                row.add(buttonOf("⏮ в главное меню", "main"));
                buttons.add(row);
                keyboards.add(buttons);
                buttons = new ArrayList<>();
                row = new ArrayList<>();
            }
        }
        return new InlineKeyboardMarkup(keyboards.get(pageNum - 1));
    }

    private InlineKeyboardButton buttonOf(String buttonText, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonText);
        button.setCallbackData(callbackData);
        return button;
    }

    private List<InlineKeyboardButton> pageButtonsRow(String handlerName, int buttonNum, int columns, int buttonsTotal) {
        int buttonsLimit = 8 * columns;
        int pagesTotal = buttonsTotal / buttonsLimit + 1;
        int currentPage = buttonNum / buttonsLimit + 1;
        String callbackFormat = "%s page %d %d %d";
        String previousCallback = callbackFormat.formatted(handlerName, currentPage, currentPage - 1, pagesTotal);
        String currentCallback = callbackFormat.formatted(handlerName, currentPage, currentPage, pagesTotal);
        String nextCallback = callbackFormat.formatted(handlerName, currentPage, currentPage + 1, pagesTotal);

        InlineKeyboardButton previous = buttonOf("◀️", previousCallback);
        InlineKeyboardButton current = buttonOf(currentPage + " / " + pagesTotal, currentCallback);
        InlineKeyboardButton next = buttonOf("▶️", nextCallback);
        return Arrays.asList(previous, current, next);
    }
}
