package edu.java.service;

import edu.java.configuration.ApplicationConfig;
import java.util.concurrent.ExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdaterSchedulerKafka implements LinkUpdaterScheduler {

    private final ScrapperQueueProducer scrapperQueueProducer;
    private final Logger logger = LogManager.getLogger();
    private final ApplicationConfig applicationConfig;

    public LinkUpdaterSchedulerKafka(ScrapperQueueProducer scrapperQueueProducer, ApplicationConfig applicationConfig) {
        this.scrapperQueueProducer = scrapperQueueProducer;
        this.applicationConfig = applicationConfig;
    }

    @Override
    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        if (applicationConfig.useQueue()) {
            try {
                var result = scrapperQueueProducer.send();
                logger.info(result);
            } catch (ExecutionException | InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
