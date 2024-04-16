package edu.java.configuration;

import edu.java.client.model.LinkUpdate;
import jakarta.validation.constraints.NotEmpty;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@ConfigurationProperties("spring.kafka")
public record KafkaConfiguration(
    String bootstrapServers,
    String clientId,
    @NotEmpty
    String acksMode,
    Duration deliveryTimeout,
    Integer lingerMs,
    Integer batchSize,
    Integer maxInFlightPerConnection,
    Boolean enableIdempotence,
    String topic
) {
    @Bean
    public ProducerFactory<String, LinkUpdate> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId());
        props.put(ProducerConfig.ACKS_CONFIG, acksMode());
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, (int) deliveryTimeout().toMillis());
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize());
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightPerConnection());
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence());
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, LinkUpdate> kafkaTemplateScrapper(
        ProducerFactory<String,
            LinkUpdate> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}
