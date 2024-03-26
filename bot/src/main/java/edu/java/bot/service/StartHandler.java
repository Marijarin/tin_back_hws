package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.model.Bot;
import edu.java.bot.service.model.BotUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartHandler implements CommandHandler {
    private final ApplicationConfig applicationConfig;
    private final ScrapperClient scrapperClient;
    Logger logger = LogManager.getLogger();

    @Autowired public StartHandler(ApplicationConfig applicationConfig, ScrapperClient scrapperClient) {
        this.applicationConfig = applicationConfig;
        this.scrapperClient = scrapperClient;
    }

    @Override
    public SendMessage handle(Bot bot, UserMessageHandler messageHandler, Update update) {
        BotUser botUser = messageHandler.extractUser(update);
        if (bot.chats().containsKey(botUser)) {
            return new SendMessage(botUser.chatId(), applicationConfig.alreadyRegistered());
        } else {
            return processStart(bot, botUser);
        }
    }

    private SendMessage processStart(Bot bot, BotUser botUser) {
            var chat = scrapperClient.findChat(botUser.chatId());
            logger.error(chat);
            if (chat.chatId() == -1) {
                scrapperClient.registerChat(botUser.chatId());
            } else {
                return new SendMessage(botUser.chatId(), applicationConfig.alreadyRegistered());
            }
        putUser(bot, botUser);
        return new SendMessage(
            botUser.chatId(),
            applicationConfig.registered() + botUser.name()
        );
    }

    @Override
    public CommandName getCommand() {
        return CommandName.START;
    }
}
