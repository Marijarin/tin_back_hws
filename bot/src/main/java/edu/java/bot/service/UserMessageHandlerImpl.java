package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.BotUser;
import edu.java.bot.model.UserMessage;
import edu.java.bot.repository.CommandName;
import edu.java.bot.repository.UserMessageHandler;
import java.util.HashMap;
import java.util.Map;

public class UserMessageHandlerImpl implements UserMessageHandler {

    Map<String, CommandName> commands = new HashMap<>();

    UserMessageHandlerImpl() {
        addCommands();
    }

    @Override
    public BotUser extractUser(Update update) {
        return new BotUser(
            update.message().from().id(),
            update.message().chat().id(),
            update.message().from().username(),
            update.message().from().isBot()
        );
    }

    @Override
    public UserMessage convert(Update update) {
        return new UserMessage(
            update.message().messageId(),
            update.message().chat().id(),
            extractUser(update),
            update.message().text()
        );
    }

    @Override
    public boolean isCommand(UserMessage userMessage) {
        return commands.containsKey(userMessage.text());
    }

    @Override
    public SendMessage listCommands(UserMessage userMessage) {
        return new SendMessage(userMessage.chatId(), prettyPrint());
    }

    private void addCommands() {
        CommandName[] cNames = CommandName.values();
        for (CommandName c : cNames) {
            commands.put(c.getCommand(), c);
        }
    }

    String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        for (String c : commands.keySet()) {
            sb.append("\n");
            sb.append(c);
        }
        return sb.toString();
    }
}
