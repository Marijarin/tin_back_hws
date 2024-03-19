package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.model.Bot;
import edu.java.bot.service.model.BotUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListHandler implements CommandHandler {
    private final ApplicationConfig applicationConfig;
    private final ScrapperClient scrapperClient;

    private final boolean isTest;

    @Autowired
    private ListHandler(ApplicationConfig applicationConfig, ScrapperClient scrapperClient) {
        this.applicationConfig = applicationConfig;
        this.scrapperClient = scrapperClient;
        this.isTest = false;
    }

    public ListHandler(ApplicationConfig applicationConfig, boolean isTest) {
        this.applicationConfig = applicationConfig;
        this.scrapperClient = null;
        this.isTest = isTest;
    }

    @Override
    public SendMessage handle(Bot bot, UserMessageHandler messageHandler, Update update) {
        BotUser botUser = messageHandler.extractUser(update);
        if (isBotHaving(bot, botUser)) {
            bot.chats().get(botUser).links().addAll(processScrapperResponse(botUser.chatId()));
            return listLinks(botUser, bot);
        } else {
            return checkDB(bot, botUser);
        }
    }

    @Override
    public CommandName getCommand() {
        return CommandName.LIST;
    }

    private List<String> processScrapperResponse(long id) {
        if (!isTest) {
            assert scrapperClient != null;
            var linkResponseList = scrapperClient.getLinksFromTG(id);
            if (linkResponseList.size() > 0) {
                return Optional
                    .of(linkResponseList.links().stream().map(it -> it.url().toString()).toList())
                    .orElseGet(ArrayList::new);
            }
        }
        return List.of();
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

    private SendMessage checkDB(Bot bot, BotUser botUser) {
        if (!isTest) {
            assert scrapperClient != null;
            var chatDB = scrapperClient.findChat(botUser.chatId());
            if (chatDB.chatId() == -1) {
                return askToRegister(botUser.chatId());
            }
            putUser(bot, botUser);
            return listLinks(botUser, bot);
        }
        return askToRegister(botUser.chatId());
    }

}
