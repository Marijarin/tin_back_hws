package edu.java.bot.model;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.repository.CommandName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

public record Bot(
    TelegramBot bot,
    Map<BotUser, Chat> chats,
    Map<BotUser, CommandName> isWaiting
) {
}
