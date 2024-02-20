package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.model.Bot;
import edu.java.bot.model.BotUser;
import edu.java.bot.repository.CommandName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("TRACK")
public class TrackHandler implements CommandHandler {
    @Autowired
    private ApplicationConfig applicationConfig;

    @Override
    public SendMessage handle(Bot bot, UserMessageHandler messageHandler, Update update) {
        BotUser botUser = messageHandler.extractUser(update);
        if (isBotHaving(bot, botUser)) {
            return waitForALink(botUser, bot);
        } else {
            return askToRegister(botUser.chatId());
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
}
