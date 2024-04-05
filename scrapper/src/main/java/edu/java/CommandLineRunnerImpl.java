package edu.java;

import edu.java.configuration.ApplicationConfig;
import edu.java.service.ScrapperQueueProducer;
import java.util.concurrent.ExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component public class CommandLineRunnerImpl implements CommandLineRunner {
    Logger logger = LogManager.getLogger();
    private final ApplicationConfig applicationConfig;

    private final ScrapperQueueProducer scrapperQueueProducer;

    public CommandLineRunnerImpl(ApplicationConfig applicationConfig, ScrapperQueueProducer scrapperQueueProducer) {
        this.applicationConfig = applicationConfig;
        this.scrapperQueueProducer = scrapperQueueProducer;
    }

    @SuppressWarnings({"MagicNumber", "MultipleStringLiterals"}) @Override public void run(String... args)
        throws ExecutionException, InterruptedException {
        logger.info("-> -> -> " + applicationConfig.databaseAccessType());
    }
}
