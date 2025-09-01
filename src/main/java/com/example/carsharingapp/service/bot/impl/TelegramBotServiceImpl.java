package com.example.carsharingapp.service.bot.impl;

import com.example.carsharingapp.service.bot.TelegramBotService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class TelegramBotServiceImpl extends TelegramLongPollingBot implements TelegramBotService {

    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return "";
    }
}
