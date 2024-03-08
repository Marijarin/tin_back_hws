package edu.java.bot.service.model;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.repository.CommandName;
import java.util.Map;

public record Bot(
        TelegramBot bot,
        Map<BotUser, Chat> chats,
        Map<BotUser, CommandName> isWaiting
) {
}
