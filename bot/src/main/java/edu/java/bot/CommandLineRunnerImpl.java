package edu.java.bot;

import edu.java.bot.client.ScrapperClient;
import edu.java.bot.service.PenBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    private final PenBot penBot;

    Logger logger = LogManager.getLogger();

    @Autowired
    public CommandLineRunnerImpl(PenBot penBot) {
        this.penBot = penBot;
    }

    @Autowired ScrapperClient scrapperClient;

    @SuppressWarnings("MagicNumber")
    @Override
    public void run(String... args) {
        penBot.start();
        logger.info("started");
    }
}
