package edu.java.configuration;

import edu.java.client.BotClient;
import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Configuration
@SuppressWarnings("MagicNumber")
public class ClientConfiguration {
    private final ApplicationConfig applicationConfig;

    private final Map<String, ExchangeFilterFunction> filters;

    @Autowired
    public ClientConfiguration(ApplicationConfig applicationConfig, Map<String, ExchangeFilterFunction> filters) {
        this.applicationConfig = applicationConfig;
        this.filters = filters;
    }

    Logger logger = LogManager.getLogger();

    @SuppressWarnings("MagicNumber")
    @Bean
    @Scope("prototype")
    WebClient webClient(String baseUrl) {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .exchangeStrategies(ExchangeStrategies
                .builder()
                .codecs(codecs -> codecs
                    .defaultCodecs()
                    .maxInMemorySize(500 * 1024))
                .build())
            .defaultStatusHandler(
                HttpStatusCode::isError,
                clientResponse -> {
                    logger.error("Error! from scrapper api");
                    return Mono.empty();
                }
            )
            .build();
    }

    @Bean
    StackOverflowClient stackOverflowClient() {
        var wc = WebClientAdapter
            .create(webClient(applicationConfig.baseUrlStackOverflow())
                .mutate()
                .filter(filters.get(applicationConfig.typeConstant()))
                .build());
        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory
                .builderFor(wc)
                .build();
        return httpServiceProxyFactory.createClient(StackOverflowClient.class);
    }

    @Bean
    GitHubClient gitHubClient() {
        var wc = WebClientAdapter
            .create(webClient(applicationConfig.baseUrlGitHub())
                .mutate()
                .filter(filters.get(applicationConfig.typeExponential()))
                .build());
        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory
                .builderFor(wc)
                .build();
        return httpServiceProxyFactory
            .createClient(GitHubClient.class);
    }

    @Bean
    BotClient botClient() {
        var wc = WebClientAdapter
            .create(webClient(applicationConfig.baseUrlBot())
                .mutate()
                .filter(filters.get(applicationConfig.typeLinear()))
                .build());
        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory
                .builderFor(wc)
                .build();
        return httpServiceProxyFactory
            .createClient(BotClient.class);
    }
}
