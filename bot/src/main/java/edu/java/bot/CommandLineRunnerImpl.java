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
    @Autowired PenBot penBot;
    @Autowired ScrapperClient scrapperClient;

    Logger logger = LogManager.getLogger();
    @Override
    public void run(String... args) throws Exception {
        penBot.start();
        Thread.sleep(10000);
        var result1 = scrapperClient.startLinkTracking(1L, "hhhh");
        var result2 = scrapperClient.registerChat(2L);
        logger.info(result1);
        logger.info(result2);
    }
}
