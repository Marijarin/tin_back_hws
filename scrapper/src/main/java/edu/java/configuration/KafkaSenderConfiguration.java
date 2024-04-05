package edu.java.configuration;

import edu.java.service.LinkUpdaterScheduler;
import edu.java.service.LinkUpdaterSchedulerKafka;
import edu.java.service.ScrapperQueueProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "true")
public class KafkaSenderConfiguration {
    @Bean
    public LinkUpdaterScheduler linkUpdaterScheduler(
        ScrapperQueueProducer scrapperQueueProducer,
        ApplicationConfig applicationConfig
    ) {
        return new LinkUpdaterSchedulerKafka(scrapperQueueProducer, applicationConfig);
    }
}
