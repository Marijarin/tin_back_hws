package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.model.Bot;
import edu.java.bot.model.BotUser;
import edu.java.bot.model.Chat;
import edu.java.bot.model.UserMessage;
import edu.java.bot.repository.BotProcessor;
import edu.java.bot.repository.CommandHandler;
import edu.java.bot.repository.CommandName;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(ApplicationConfig.class)
public class PenBot implements BotProcessor, CommandHandler {

    private final Bot bot;
    private final ApplicationConfig applicationConfig;
    private final UserMessageHandlerImpl messageHandler = new UserMessageHandlerImpl();

    @Autowired
    private PenBot(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        String token = applicationConfig.telegramToken();
        bot = new Bot(new TelegramBot(token), new HashMap<>(), new HashMap<>());
    }

    public PenBot(Bot bot, ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        this.bot = bot;
    }

    @Override
    public void processUserRequest(Update upd) {
        UserMessage userMessage = messageHandler.convert(upd);
        if (messageHandler.isCommand(userMessage)) {
            String c = userMessage.text();
            CommandName command = messageHandler.commands.get(c).commandName();
            bot.bot().execute(apply(command).apply(upd));
        } else {
            bot.bot().execute(apply(null).apply(upd));
        }
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::processUserRequest);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Override
    @PostConstruct
    public void start() {
        bot.bot().setUpdatesListener(this);
    }

    @Override
    public void close() {
        bot.bot().removeGetUpdatesListener();
    }

    @SuppressWarnings("ReturnCount")
    @Override
    public Function<Update, SendMessage> apply(CommandName command) {
        switch (command) {
            case START -> {
                return update -> registerUser(messageHandler.extractUser(update));
            }
            case HELP -> {
                return update -> messageHandler.listCommands(messageHandler.convert(update));
            }
            case LIST -> {
                return update -> {
                    BotUser botUser = messageHandler.extractUser(update);
                    if (isBotHaving(botUser)) {
                        return listLinks(botUser);
                    } else {
                        return askToRegister(botUser.chatId());
                    }
                };
            }
            case TRACK, UNTRACK -> {
                return update -> {
                    BotUser botUser = messageHandler.extractUser(update);
                    if (isBotHaving(botUser)) {
                        return waitForALink(botUser, command);
                    } else {
                        return askToRegister(botUser.chatId());
                    }
                };
            }
            case null, default -> {
                return update -> {
                    BotUser botUser = messageHandler.extractUser(update);
                    String text = messageHandler.convert(update).text();
                    if (isBotHaving(botUser) && bot.isWaiting().get(botUser) != null) {
                        return convertToLink(botUser, text, bot.isWaiting().get(botUser));
                    } else {
                        return invalidUserMessage(botUser.chatId());
                    }
                };
            }
        }
    }

    public SendMessage registerUser(BotUser botUser) {
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

    public SendMessage waitForALink(BotUser botUser, CommandName command) {
        bot.isWaiting().replace(botUser, command);
        return new SendMessage(
            botUser.chatId(),
            applicationConfig.sendLink()
        );
    }

    public SendMessage listLinks(BotUser botUser) {
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

    public SendMessage convertToLink(BotUser botUser, String text, CommandName command) {
        bot.isWaiting().replace(botUser, null);
        Pattern pattern = Pattern.compile(applicationConfig.pattern());
        Matcher ulrMatcher = pattern.matcher(text.trim());
        if (ulrMatcher.find()) {
            return switch (command) {
                case TRACK -> processTrack(botUser, text);
                case UNTRACK -> processUnTrack(botUser, text);
                case null, default -> invalidUserMessage(botUser.chatId());
            };
        } else {
            return invalidUserMessage(botUser.chatId());
        }
    }

    public SendMessage processTrack(BotUser botUser, String url) {
        bot.isWaiting().replace(botUser, null);
        bot.chats().get(botUser).links().add(url);
        return new SendMessage(
            botUser.chatId(),
            applicationConfig.done() + url
        );
    }

    public SendMessage processUnTrack(BotUser botUser, String url) {
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

    public boolean isBotHaving(BotUser botUser) {
        return bot.chats().containsKey(botUser);
    }

    public SendMessage askToRegister(long chatId) {
        return new SendMessage(
            chatId,
            applicationConfig.register()
        );
    }

    public SendMessage invalidUserMessage(long chatId) {
        return new SendMessage(
            chatId,
            applicationConfig.notUnderstand()
        );
    }

}
