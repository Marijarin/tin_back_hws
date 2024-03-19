package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.model.Bot;
import edu.java.bot.service.model.BotUser;
import edu.java.bot.service.model.Chat;
import java.util.HashSet;

public interface CommandHandler {
    SendMessage handle(Bot bot, UserMessageHandler userMessageHandler, Update update);

    default boolean isBotHaving(Bot bot, BotUser botUser) {
        return bot.chats().containsKey(botUser);
    }

    default void putUser(Bot bot, BotUser botUser) {
        Chat chat = new Chat(
            botUser.chatId(),
            botUser.id(),
            botUser.name(),
            new HashSet<>()
        );
        bot.chats().put(botUser, chat);
        bot.isWaiting().put(botUser, null);
    }
    CommandName getCommand();

    default String getBeanName() {
        return getCommand().name();
    }

}
