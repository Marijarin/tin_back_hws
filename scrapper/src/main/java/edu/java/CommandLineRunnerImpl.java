package edu.java;

import edu.java.configuration.ApplicationConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component public class CommandLineRunnerImpl implements CommandLineRunner {
    Logger logger = LogManager.getLogger();
    private final ApplicationConfig applicationConfig;

    public CommandLineRunnerImpl(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @SuppressWarnings({"MagicNumber", "MultipleStringLiterals"}) @Override public void run(String... args) {
        logger.info("-> -> -> " + applicationConfig.databaseAccessType());
    }
}
