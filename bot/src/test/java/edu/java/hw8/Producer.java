package edu.java.hw8;

import edu.java.bot.controller.dto.LinkUpdate;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


public class Producer {
    private final Logger logger = LogManager.getLogger();

    @Value("${test.topic}")
    private String topic;

    private final LinkUpdate goodUpdate =
        new LinkUpdate(1L, URI.create("https://stackoverflow.com"), "12345", new long[0]);

    private final KafkaTemplate<String, LinkUpdate> template;

    public Producer(KafkaTemplate<String, LinkUpdate> template) {
        this.template = template;
    }

    public List<LinkUpdate> sendWell() {
        template
            .send(
                topic,
                1,
                OffsetDateTime.now().toString(),
                goodUpdate
            );
        logger.info("sent well!");
        return List.of(goodUpdate);
    }

    public List<LinkUpdate> sendBadly() {
        template
            .send(
                topic,
                goodUpdate
            );
        logger.info("sent badly!");
        return List.of(goodUpdate);
    }
}
