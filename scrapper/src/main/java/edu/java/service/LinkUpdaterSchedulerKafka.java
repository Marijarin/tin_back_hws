package edu.java.service;

import java.util.concurrent.ExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdaterSchedulerKafka implements LinkUpdaterScheduler {

    private final ScrapperQueueProducer scrapperQueueProducer;
    private final Logger logger = LogManager.getLogger();

    public LinkUpdaterSchedulerKafka(ScrapperQueueProducer scrapperQueueProducer) {
        this.scrapperQueueProducer = scrapperQueueProducer;
    }

    @Override
    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        try {
            var result = scrapperQueueProducer.send();
            logger.info(result);
        } catch (ExecutionException | InterruptedException e) {
            logger.error(e.getMessage());
        }
    }
}
