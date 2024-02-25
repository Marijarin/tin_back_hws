package edu.java;

import edu.java.service.GitHubClient;
import edu.java.service.StackOverflowClient;
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

    @Override
    public void run(String... args) throws Exception {
        var result1 = gitHubClient
            .getResponse("Marijarin", "tin_back_hws")
            .toFuture().get()
            .getFirst()
            .createdAt();
        logger.info(result1);
        var result2 = stackOverflowClient
            .getResponse("75867589")
            .toFuture().get()
            .items()
            .getFirst()
            .creationDate();
        logger.info(result2);
    }
}
