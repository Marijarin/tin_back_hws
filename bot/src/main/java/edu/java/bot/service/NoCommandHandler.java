package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.model.Bot;
import edu.java.bot.model.BotUser;
import edu.java.bot.repository.CommandName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("NOCOMMAND")
public class NoCommandHandler implements CommandHandler {
    @Autowired
    ApplicationConfig applicationConfig;

    @Override
    public SendMessage handle(Bot bot, UserMessageHandler messageHandler, Update update) {
        BotUser botUser = messageHandler.extractUser(update);
        String text = messageHandler.convert(update).text();
        if (isBotHaving(bot, botUser) && bot.isWaiting().get(botUser) != null) {
            return convertToLink(botUser, text, bot);
        } else {
            return invalidUserMessage(botUser.chatId());
        }
    }

    @Override
    public CommandName getCommand() {
        return CommandName.NOCOMMAND;
    }

    public SendMessage invalidUserMessage(long chatId) {
        return new SendMessage(
            chatId,
            applicationConfig.notUnderstand()
        );
    }
    public SendMessage convertToLink(BotUser botUser, String text, Bot bot) {
        var command = bot.isWaiting().get(botUser);
        bot.isWaiting().replace(botUser, null);
        Pattern pattern = Pattern.compile(applicationConfig.pattern());
        Matcher ulrMatcher = pattern.matcher(text.trim());
        if (ulrMatcher.find()) {
            return switch (command) {
                case TRACK -> processTrack(botUser, text, bot);
                case UNTRACK -> processUnTrack(botUser, text, bot);
                case null, default -> invalidUserMessage(botUser.chatId());
            };
        } else {
            return invalidUserMessage(botUser.chatId());
        }
    }
    public SendMessage processTrack(BotUser botUser, String url, Bot bot) {
        bot.isWaiting().replace(botUser, null);
        if (!bot.chats().get(botUser).links().contains(url)) {
            bot.chats().get(botUser).links().add(url);
        }
        return new SendMessage(
            botUser.chatId(),
            applicationConfig.done() + url
        );
    }
    public SendMessage processUnTrack(BotUser botUser, String url, Bot bot) {
        bot.isWaiting().replace(botUser, null);
        var list = bot.chats().get(botUser).links();
        if (list.remove(url)) {
            return new SendMessage(
                botUser.chatId(),
                applicationConfig.done() + Arrays.deepToString(bot.chats().get(botUser).links().toArray())
            );
        }
        return new SendMessage(
            botUser.chatId(),
            applicationConfig.notTracked() + url
        );
    }
}
