package edu.java.hw7;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.controller.dto.ApiErrorResponse;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@WireMockTest
public class ScrapperClientTest {
    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
        .options(wireMockConfig().port(8080))
        .build();
    WebClient webClient1 = WebClient.builder()
        .baseUrl("http://localhost:8080")
        .exchangeStrategies(ExchangeStrategies
            .builder()
            .codecs(codecs -> codecs
                .defaultCodecs()
                .maxInMemorySize(500 * 1024))
            .build())
        .build();

    ExchangeFilterFunction linear() {
        return (request, next) -> next.exchange(request)
            .flatMap(clientResponse -> Mono.just(clientResponse)
                .filter(response -> clientResponse.statusCode().isError())
                .flatMap(response -> clientResponse.toEntity(ApiErrorResponse.class))
                .flatMap(mono -> {
                    var body = Optional.ofNullable(mono.getBody()).orElseThrow();
                    var e = body.exception();
                    return Mono.error(e);
                })
                .thenReturn(clientResponse))
            .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1)));
    }

    @Test
    public void retries2TimesWhenLinear() {
        HttpExchangeAdapter wc = WebClientAdapter
            .create(webClient1
                .mutate()
                .filter(linear())
                .build());

        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory
                .builderFor(wc)
                .build();
        var client = httpServiceProxyFactory.createClient(ScrapperClient.class);
        wm.stubFor(get(urlPathMatching("/tg-chat/1000"))
            .willReturn(aResponse()
                .withStatus(400)));
        try {
            client.findChat(1000L);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        wm.verify(1 + 2, getRequestedFor(urlPathMatching("/tg-chat/1000")));
    }
}
