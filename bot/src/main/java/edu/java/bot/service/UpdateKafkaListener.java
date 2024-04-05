package edu.java.bot.service;

import edu.java.bot.controller.dto.LinkUpdate;
import edu.java.bot.service.model.SendUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateKafkaListener {

    private final PenBot penBot;

    public UpdateKafkaListener(PenBot penBot) {
        this.penBot = penBot;
    }


    @KafkaListener(topics = "messages.string",
                   groupId = "bot",
                   containerFactory = "updateKafkaListenerContainerFactory",
                   concurrency = "1")
    public void listenUpdate(
        @Payload LinkUpdate linkUpdate,
        @Header(KafkaHeaders.RECEIVED_KEY) String key,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {
        log.info("Received Message from partition {} with key {}: {}", partition, key, linkUpdate);
        var sendUpdate = new SendUpdate(
            linkUpdate.url(),
            linkUpdate.description(),
            linkUpdate.tgChatIds(),
            linkUpdate.description());
        penBot.processUpdateFromScrapper(sendUpdate);
        log.info("Done!");
    }
}
