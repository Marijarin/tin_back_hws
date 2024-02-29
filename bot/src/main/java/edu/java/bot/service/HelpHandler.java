package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.model.Bot;
import edu.java.bot.repository.CommandName;
import org.springframework.stereotype.Component;

@Component
public class HelpHandler implements CommandHandler {

    @Override
    public SendMessage handle(Bot bot, UserMessageHandler messageHandler, Update update) {
        return messageHandler.listCommands(messageHandler.convert(update));
    }

    @Override
    public CommandName getCommand() {
        return CommandName.HELP;
    }

}
