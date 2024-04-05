package edu.java.bot.service;

import edu.java.bot.controller.dto.LinkUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateKafkaListener {

    @KafkaListener(topics = "messages.string",
                   groupId = "bot",
                   containerFactory = "updateKafkaListenerContainerFactory",
                   concurrency = "1")
    public void listenUpdate(
        @Payload LinkUpdate message,
        @Header(KafkaHeaders.RECEIVED_KEY) String key,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {
        log.info("Received Message from partition {} with key {}: {}", partition, key, message);
    }
}
