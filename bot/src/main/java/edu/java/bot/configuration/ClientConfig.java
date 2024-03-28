package edu.java.bot.configuration;

import edu.java.bot.client.ScrapperClient;
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
import java.util.Map;

@Configuration
public class ClientConfig {
    private final ApplicationConfig applicationConfig;

    private final Map<String, ExchangeFilterFunction> filters;

    Logger logger = LogManager.getLogger();

    @Autowired
    public ClientConfig(ApplicationConfig applicationConfig, Map<String, ExchangeFilterFunction> filters) {
        this.applicationConfig = applicationConfig;
        this.filters = filters;
    }

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
                    logger.error("Error! from bot api");
                    return Mono.empty();
                }
            )
            .build();
    }

    @Bean
    ScrapperClient scrapperClient() {
        var wc = WebClientAdapter
            .create(webClient(applicationConfig.baseUrlScrapper())
                .mutate()
                .filter(filters.get(applicationConfig.typeLinear()))
                .build());
        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory
                .builderFor(wc)
                .build();
        return httpServiceProxyFactory.createClient(ScrapperClient.class);
    }

}

