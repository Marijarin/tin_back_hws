package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.Bot;
import edu.java.bot.model.BotUser;
import edu.java.bot.repository.CommandName;

public interface CommandHandler {
    SendMessage handle(Bot bot, UserMessageHandler userMessageHandler, Update update);
default boolean isBotHaving(Bot bot, BotUser botUser){
    return bot.chats().containsKey(botUser);
}
    CommandName getCommand();
}
