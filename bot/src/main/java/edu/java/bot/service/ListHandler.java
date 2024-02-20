package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.model.Bot;
import edu.java.bot.model.BotUser;
import edu.java.bot.repository.CommandName;
import java.util.Arrays;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("LIST")
public class ListHandler implements CommandHandler {
    @Autowired
    ApplicationConfig applicationConfig;
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

    public SendMessage listLinks(BotUser botUser, Bot bot) {
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
    public SendMessage askToRegister(long chatId) {
        return new SendMessage(
            chatId,
            applicationConfig.register()
        );
    }

}
