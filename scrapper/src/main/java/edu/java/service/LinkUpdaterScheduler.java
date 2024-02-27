package edu.java.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LinkUpdaterScheduler {
    Logger logger = LogManager.getLogger();

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    void update() {
        logger.info("Updated");
    }
}
