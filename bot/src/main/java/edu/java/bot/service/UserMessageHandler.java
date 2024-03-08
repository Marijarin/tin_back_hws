package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.model.BotUser;
import edu.java.bot.service.model.UserMessage;

public interface UserMessageHandler {
    BotUser extractUser(Update update);

    UserMessage convert(Update update);

    boolean isCommand(UserMessage userMessage);

    SendMessage listCommands(UserMessage userMessage);
}
