package edu.java.bot.repository;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import java.util.List;

public interface BotProcessor extends AutoCloseable, UpdatesListener {

    void processUserRequest(Update update);

    @Override
    int process(List<Update> updates);

    void start();

    @Override
    void close();
}
