package edu.java.hw8;

import edu.java.bot.controller.dto.LinkUpdate;
import edu.java.bot.service.PenBot;
import edu.java.bot.service.UpdateKafkaListener;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {UpdateKafkaListener.class, KafkaTestConfiguration.class})
@Testcontainers
public class KafkaConsumerTest {
    @Container
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

    @Autowired
    public KafkaTemplate<String, LinkUpdate> template;

    @Autowired
    private UpdateKafkaListener listener;

    @MockBean PenBot penBot;

    @Autowired
    private KafkaProducer<String, LinkUpdate> producer;

    @Value("${test.topic}")
    private String topic;

    private final LinkUpdate goodUpdate =
        new LinkUpdate(1L, URI.create("https://stackoverflow.com"), "12345", new long[0]);
    private final LinkUpdate badUpdate = null;

    @BeforeEach
    void resetLatch() {
        listener.resetLatch();
    }

    @Test
    void listenerGetsGoodMessage() throws InterruptedException {
        ProducerRecord<String, LinkUpdate> record = new ProducerRecord<>(topic, goodUpdate);
        assertThat(producer).isNotNull();
        producer.send(record);
        listener.listenUpdate(goodUpdate, "", 0);
        assertThat(listener).isNotNull();
        boolean messageConsumed = listener.getLatch().await(10, TimeUnit.SECONDS);
        assertTrue(messageConsumed);
    }

    @Test
    void listenerDoesNotGetBadMessage() throws InterruptedException {
        ProducerRecord<String, LinkUpdate> record = new ProducerRecord<>(topic, badUpdate);
        assertThat(producer).isNotNull();
        producer.send(record);
        listener.listenUpdate(badUpdate, "", 0);
        boolean messageConsumed = listener.getLatch().await(10, TimeUnit.SECONDS);
        assertFalse(messageConsumed);
    }
}
