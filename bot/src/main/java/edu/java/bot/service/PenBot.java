package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.repository.CommandName;
import edu.java.bot.service.model.Bot;
import edu.java.bot.service.model.SendUpdate;
import edu.java.bot.service.model.UserMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import static java.util.stream.Collectors.toMap;

@Service
@EnableConfigurationProperties(ApplicationConfig.class)
public class PenBot implements BotProcessor {
    private final Bot bot;
    private final ApplicationConfig applicationConfig;
    private final UserMessageHandlerImpl messageHandler;

    private final Map<String, CommandHandler> commandHandlers;

    private final UpdateHandler updateHandler;

    @Autowired
    private PenBot(
        ApplicationConfig applicationConfig,
        List<CommandHandler> commandHandlersList,
        UserMessageHandlerImpl messageHandler, UpdateHandler updateHandler
    ) {
        this.applicationConfig = applicationConfig;
        this.commandHandlers = commandHandlersList
            .stream()
            .collect(toMap(CommandHandler::getBeanName, commandHandler -> commandHandler));
        this.messageHandler = messageHandler;
        this.updateHandler = updateHandler;
        String token = applicationConfig.telegramToken();
        bot = new Bot(new TelegramBot(token), new HashMap<>(), new HashMap<>());
    }

    public PenBot(
        Bot bot,
        ApplicationConfig applicationConfig,
        UserMessageHandlerImpl messageHandler,
        Map<String, CommandHandler> commandHandlers
    ) {
        this.applicationConfig = applicationConfig;
        this.bot = bot;
        this.messageHandler = messageHandler;
        this.commandHandlers = commandHandlers;
        this.updateHandler = null;
    }

    @Override
    public void processUserRequest(Update upd) {
        UserMessage userMessage = messageHandler.convert(upd);
        String c = userMessage.text();
        CommandName command;
        if (messageHandler.isCommand(userMessage)) {
            command = messageHandler.getCommands().get(c).commandName();
        } else {
            command = CommandName.NOCOMMAND;
        }
        bot.bot().execute(commandHandlers.get(command.toString()).handle(bot, messageHandler, upd));
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::processUserRequest);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Override
    public void start() {
        bot.bot().setUpdatesListener(this);
    }

    @Override
    public void close() {
        bot.bot().removeGetUpdatesListener();
    }

    @Override
    public void processUpdateFromScrapper(SendUpdate sendUpdate) {
        var chats = sendUpdate.tgChatIds();
        for (long chat : chats) {
            bot.bot().execute(updateHandler.sendUpdate(sendUpdate, chat));
        }
    }
}
