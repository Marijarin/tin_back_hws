package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.model.Bot;
import edu.java.bot.model.BotUser;
import edu.java.bot.model.Chat;
import edu.java.bot.repository.CommandName;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("START")
public class StartHandler implements CommandHandler {
    @Autowired
    ApplicationConfig applicationConfig;
    @Override
    public SendMessage handle(Bot bot, UserMessageHandler messageHandler, Update update) {
        BotUser botUser = messageHandler.extractUser(update);
        if (bot.chats().containsKey(botUser)) {
            return new SendMessage(botUser.chatId(), applicationConfig.alreadyRegistered());
        } else {
            Chat chat = new Chat(
                botUser.chatId(),
                botUser.id(),
                botUser.name(),
                new ArrayList<>()
            );
            bot.chats().put(botUser, chat);
            bot.isWaiting().put(botUser, null);
            return new SendMessage(
                botUser.chatId(),
                applicationConfig.registered() + botUser.name()
            );
        }
    }

    @Override
    public CommandName getCommand() {
        return CommandName.START;
    }
}
