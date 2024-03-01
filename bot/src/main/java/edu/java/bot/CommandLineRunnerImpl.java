package edu.java.bot;

import edu.java.bot.client.ScrapperClient;
import edu.java.bot.client.model.AddLinkRequest;
import edu.java.bot.service.PenBot;
import java.net.URI;
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

    @SuppressWarnings("MagicNumber")
    @Override
    public void run(String... args) throws Exception {
        penBot.start();
        Thread.sleep(10000);
        //var result = scrapperClient.registerChat(1L);
        AddLinkRequest linkRequest =
            new AddLinkRequest(URI.create(
                "https://stackoverflow.com/questions/49132346/spring-rest-controller-string-response"));
        var result1 = scrapperClient.startLinkTracking(1L, linkRequest);
        var result2 = scrapperClient.registerChat(2L);
        var result3 = scrapperClient.registerChat(2L);
        logger.info(result1);
        logger.info(result2);
        logger.info(result3);
    }
}
