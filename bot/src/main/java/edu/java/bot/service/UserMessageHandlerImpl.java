package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.BotUser;
import edu.java.bot.model.CustomCommand;
import edu.java.bot.model.UserMessage;
import edu.java.bot.repository.CommandName;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class UserMessageHandlerImpl implements UserMessageHandler {

    private final Map<String, CustomCommand> commands = new HashMap<>();

    public UserMessageHandlerImpl() {
        addCommands();
    }

    @Override
    public BotUser extractUser(Update update) {
        var user = update.message().from();
        var chat = update.message().chat();
        if (user.isBot() == null) {
            return new BotUser(
                user.id(),
                chat.id(),
                user.username(),
                true
            );
        }
        return new BotUser(
            user.id(),
            chat.id(),
            user.username(),
            user.isBot()
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

    public Map<String, CustomCommand> getCommands() {
        return commands;
    }

    private void addCommands() {
        CustomCommand[] cNames = CommandName.getCommandsWithDescriptions();
        for (CustomCommand c : cNames) {
            commands.put(c.commandName().getCommand(), c);
        }
    }

    private String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        for (String c : commands.keySet()) {
            if (c.isEmpty() || c.isBlank()) {
                continue;
            }
            sb.append("\n");
            sb.append(c);
            sb.append(" - ");
            sb.append(commands.get(c).description());
        }
        return sb.toString();
    }
}
