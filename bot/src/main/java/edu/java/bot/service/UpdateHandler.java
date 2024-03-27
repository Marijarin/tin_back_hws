package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.model.SendUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateHandler {
    private final ApplicationConfig applicationConfig;

    @Autowired
    public UpdateHandler(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    SendMessage sendUpdate(SendUpdate sendUpdate, long chatId) {
        return new SendMessage(
            chatId,
            applicationConfig.seeUpdate() + "\n" + sendUpdate.eventDescription() + "\n" + sendUpdate.url()
        );
    }
}

