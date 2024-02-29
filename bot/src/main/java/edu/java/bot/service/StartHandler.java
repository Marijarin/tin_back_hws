package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.model.Bot;
import edu.java.bot.service.model.BotUser;
import edu.java.bot.service.model.Chat;
import edu.java.bot.repository.CommandName;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartHandler implements CommandHandler {
    private final ApplicationConfig applicationConfig;

    @Autowired
    private StartHandler(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public StartHandler(ApplicationConfig applicationConfig, boolean isTest) {
        this.applicationConfig = applicationConfig;
    }

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
