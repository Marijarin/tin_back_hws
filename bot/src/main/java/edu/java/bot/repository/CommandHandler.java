package edu.java.bot.repository;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.function.Function;

public interface CommandHandler extends Function<CommandName, Function<Update, SendMessage>> {

}
