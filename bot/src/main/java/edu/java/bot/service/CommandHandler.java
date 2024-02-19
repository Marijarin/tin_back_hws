package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repository.CommandName;
import java.util.function.Function;

public interface CommandHandler extends Function<CommandName, Function<Update, SendMessage>> {

}
