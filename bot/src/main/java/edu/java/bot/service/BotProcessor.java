package edu.java.bot.service;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.service.model.SendUpdate;
import java.util.List;

public interface BotProcessor extends AutoCloseable, UpdatesListener {

    void processUserRequest(Update update);

    @Override
    int process(List<Update> updates);

    void start();

    @Override
    void close();

    void processUpdateFromScrapper(SendUpdate sendUpdate);
}
