package edu.java.bot;

import edu.java.bot.client.ScrapperClient;
import edu.java.bot.client.model.AddLinkRequest;
import edu.java.bot.client.model.RemoveLinkRequest;
import edu.java.bot.service.PenBot;
import java.net.URI;
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
    public CommandLineRunnerImpl(PenBot penBot, ScrapperClient scrapperClient) {
        this.penBot = penBot;
    }

    @Autowired ScrapperClient scrapperClient;

    @SuppressWarnings("MagicNumber")
    @Override
    public void run(String... args) throws Exception {
       penBot.start();
//        Thread.sleep(10000);
//        scrapperClient.registerChat(1L);
//        AddLinkRequest linkRequest =
//            new AddLinkRequest(URI.create(
//                "https://stackoverflow.com/questions/49132346/spring-rest-controller-string-response"));
//        scrapperClient.startLinkTracking(1L, linkRequest);
//        scrapperClient.registerChat(2L);
//        scrapperClient.registerChat(2L);
//        scrapperClient.startLinkTracking(2L, linkRequest);
//        scrapperClient.startLinkTracking(2L, linkRequest);
//        scrapperClient.stopLinkTracking(2L, new RemoveLinkRequest(linkRequest.link()));
//        scrapperClient.deleteChat(2L);
//        scrapperClient.deleteChat(2L);
//        scrapperClient.stopLinkTracking(2L, new RemoveLinkRequest(linkRequest.link()));
    }
}
