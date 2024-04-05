package edu.java.bot.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {
    private final ApplicationConfig applicationConfig;

    Logger logger = LogManager.getLogger();

    public KafkaConfiguration(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(applicationConfig.topic())
            .partitions(2)
            .replicas(1)
            .build();
    }

    @KafkaListener(id = "bot", topics = "topic1")
    public void listen(String in) {
        logger.info(in);
    }
}
