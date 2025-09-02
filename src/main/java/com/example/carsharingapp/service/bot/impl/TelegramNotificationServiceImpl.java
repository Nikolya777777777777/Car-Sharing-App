package com.example.carsharingapp.service.bot.impl;

import com.example.carsharingapp.service.bot.TelegramNotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TelegramNotificationServiceImpl implements TelegramNotificationService {
    private final TelegramClient telegramClient;
    private final String chatId;

    public TelegramNotificationServiceImpl(
            @Value("${bot.key}") String token,
            @Value("${bot.chat.id}") String chatId
    ) {
        this.telegramClient = new OkHttpTelegramClient(token);
        this.chatId = chatId;
    }

    @Override
    public void sendNotification(String messageText) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }
}