package edu.java;

import edu.java.client.BotClient;
import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.client.model.LinkUpdate;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    Logger logger = LogManager.getLogger();
    @Autowired GitHubClient gitHubClient;

    @Autowired StackOverflowClient stackOverflowClient;

    @Autowired BotClient botClient;

    @SuppressWarnings("MagicNumber")
    @Override
    public void run(String... args) throws Exception {
        var result1 = gitHubClient
            .getResponse("Marijarin", "tin_back_hws")
            .getFirst()
            .createdAt();
        logger.info(result1);
        var result2 = stackOverflowClient
            .getResponse("75867589")
            .items()
            .getFirst()
            .creationDate();
        logger.info(result2);
        var result3 = botClient
            .postUpdate(new LinkUpdate(
                1,
                URI.create(
                    "https://edu.tinkoff.ru/my-activities/courses/stream/b37f2c9a-b73c-4cc8-a092-0bcbf49faac7/exam/18329/1"),
                "fgfgfg",
                new long[] {1, 2, 3}
            ));
        logger.info(result3);
    }
}
