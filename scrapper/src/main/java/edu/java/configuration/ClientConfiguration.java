package edu.java.configuration;

import edu.java.client.BotClient;
import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Configuration
@SuppressWarnings("MagicNumber")
public class ClientConfiguration {
    private final ApplicationConfig applicationConfig;

    @Autowired
    public ClientConfiguration(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    Logger logger = LogManager.getLogger();

    @Bean
    WebClient webClientGH() {
        return WebClient.builder()
            .baseUrl(applicationConfig.baseUrlGitHub())
            .exchangeStrategies(ExchangeStrategies
                .builder()
                .codecs(codecs -> codecs
                    .defaultCodecs()
                    .maxInMemorySize(500 * 1024))
                .build())
            .defaultStatusHandler(
                HttpStatusCode::isError,
                clientResponse -> {
                    logger.error("Error! from scrapper api - GH");
                    return Mono.empty();
                }
            )
            .build();
    }

    @Bean
    WebClient webClientSOF() {
        return WebClient.builder()
            .baseUrl(applicationConfig.baseUrlStackOverflow())
            .exchangeStrategies(ExchangeStrategies
                .builder()
                .codecs(codecs -> codecs
                    .defaultCodecs()
                    .maxInMemorySize(500 * 1024))
                .build())
            .defaultStatusHandler(
                HttpStatusCode::isError,
                clientResponse -> {
                    logger.error("Error! from scrapper api - SOF");
                    return Mono.empty();
                }
            )
            .build();
    }

    @Bean
    WebClient webClientBot() {
        return WebClient.builder()
            .baseUrl(applicationConfig.baseUrlBot())
            .exchangeStrategies(ExchangeStrategies
                .builder()
                .codecs(codecs -> codecs
                    .defaultCodecs()
                    .maxInMemorySize(500 * 1024))
                .build())
            .defaultStatusHandler(
                HttpStatusCode::isError,
                clientResponse -> {
                    logger.error("Error! from scrapper api - Bot");
                    return Mono.empty();
                }
            )
            .build();
    }

    @Bean
    BotClient botClient() {
        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClientBot()))
                .build();
        return httpServiceProxyFactory
            .createClient(BotClient.class);
    }

    @Bean
    StackOverflowClient stackOverflowClient() {
        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClientSOF()))
                .build();
        return httpServiceProxyFactory.createClient(StackOverflowClient.class);
    }

    @Bean
    GitHubClient gitHubClient() {
        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClientGH()))
                .build();
        return httpServiceProxyFactory
            .createClient(GitHubClient.class);
    }
}
