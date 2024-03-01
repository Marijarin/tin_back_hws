package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.model.Bot;
import edu.java.bot.service.model.BotUser;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListHandler implements CommandHandler {
    private final ApplicationConfig applicationConfig;

    @Autowired
    private ListHandler(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public ListHandler(ApplicationConfig applicationConfig, boolean isTest) {
        this.applicationConfig = applicationConfig;
    }

    @Override
    public SendMessage handle(Bot bot, UserMessageHandler messageHandler, Update update) {
        BotUser botUser = messageHandler.extractUser(update);
        if (isBotHaving(bot, botUser)) {
            return listLinks(botUser, bot);
        } else {
            return askToRegister(botUser.chatId());
        }
    }

    @Override
    public CommandName getCommand() {
        return CommandName.LIST;
    }

    private SendMessage listLinks(BotUser botUser, Bot bot) {
        bot.isWaiting().replace(botUser, null);
        var list = bot.chats().get(botUser).links();
        if (list.isEmpty()) {
            return new SendMessage(botUser.chatId(), applicationConfig.emptyList());
        }
        return new SendMessage(
            botUser.chatId(),
            applicationConfig.linksHeader() + Arrays.deepToString(list.toArray())
        );
    }

    private SendMessage askToRegister(long chatId) {
        return new SendMessage(
            chatId,
            applicationConfig.register()
        );
    }

}
