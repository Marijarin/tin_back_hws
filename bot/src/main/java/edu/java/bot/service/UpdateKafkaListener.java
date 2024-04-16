package edu.java.bot.service;

import edu.java.bot.controller.dto.LinkUpdate;
import edu.java.bot.service.model.SendUpdate;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateKafkaListener {

    private final PenBot penBot;

    @Getter private CountDownLatch latch = new CountDownLatch(1);

    public UpdateKafkaListener(PenBot penBot) {
        this.penBot = penBot;
    }

    @RetryableTopic(attempts = "1", kafkaTemplate = "kafkaTemplateBot")
    @KafkaListener(topics = "messages.string",
                   groupId = "bot",
                   // errorHandler = "errorHandler",
                   containerFactory = "updateKafkaListenerContainerFactory",
                   concurrency = "1")
    public void listenUpdate(
        @Payload LinkUpdate linkUpdate,
        @Header(KafkaHeaders.RECEIVED_KEY) String key,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {
        try {
            log.info("Received Message from partition {} with key {}: {}", partition, key, linkUpdate);
            var sendUpdate = new SendUpdate(
                linkUpdate.url(),
                linkUpdate.description(),
                linkUpdate.tgChatIds(),
                linkUpdate.description()
            );
            penBot.processUpdateFromScrapper(sendUpdate);
            log.info("Done!");
            latch.countDown();
        } catch (RuntimeException runtimeException) {
            log.error(runtimeException.getMessage());
            listenDlt(linkUpdate);
        }
    }

    @DltHandler
    public void listenDlt(
        @Payload LinkUpdate linkUpdate
    ) {
        log.info("Received Message : {}", linkUpdate);
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
