package edu.java.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface LinkUpdaterScheduler {
    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    void update();
}
