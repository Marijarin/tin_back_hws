package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.model.Bot;
import edu.java.bot.service.model.BotUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrackHandler implements CommandHandler {
    private final ApplicationConfig applicationConfig;

    private final ScrapperClient scrapperClient;

    @Autowired public TrackHandler(ApplicationConfig applicationConfig, ScrapperClient scrapperClient) {
        this.applicationConfig = applicationConfig;
        this.scrapperClient = scrapperClient;
    }

    @Override
    public SendMessage handle(Bot bot, UserMessageHandler messageHandler, Update update) {
        BotUser botUser = messageHandler.extractUser(update);
        if (isBotHaving(bot, botUser)) {
            return waitForALink(botUser, bot);
        } else {
            return checkDB(bot, botUser);
        }
    }

    @Override
    public CommandName getCommand() {
        return CommandName.TRACK;
    }

    public SendMessage waitForALink(BotUser botUser, Bot bot) {
        bot.isWaiting().replace(botUser, getCommand());
        return new SendMessage(
            botUser.chatId(),
            applicationConfig.sendLink()
        );
    }

    public SendMessage askToRegister(long chatId) {
        return new SendMessage(
            chatId,
            applicationConfig.register()
        );
    }

    private SendMessage checkDB(Bot bot, BotUser botUser) {
        var chatDB = scrapperClient.findChat(botUser.chatId());
        if (chatDB.chatId() == -1) {
            return askToRegister(botUser.chatId());
        }
        putUser(bot, botUser);
        bot.isWaiting().replace(botUser, getCommand());
        return waitForALink(botUser, bot);
    }
}
