package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.BotUser;
import edu.java.bot.model.CustomCommand;
import edu.java.bot.model.UserMessage;
import edu.java.bot.repository.CommandName;
import edu.java.bot.repository.UserMessageHandler;
import java.util.HashMap;
import java.util.Map;

public class UserMessageHandlerImpl implements UserMessageHandler {

    Map<String, CustomCommand> commands = new HashMap<>();

    public UserMessageHandlerImpl() {
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
        CustomCommand[] cNames = CommandName.getCommandsWithDescriptions();
        for (CustomCommand c : cNames) {
            commands.put(c.commandName().getCommand(), c);
        }
    }

    String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        for (String c : commands.keySet()) {
            sb.append("\n");
            sb.append(c);
            sb.append(" - ");
            sb.append(commands.get(c).description());
        }
        return sb.toString();
    }
}
