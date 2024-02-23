package edu.java.configuration;

import edu.java.service.GitHubClient;
import edu.java.service.StackOverflowClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfiguration {
    @Bean
    WebClient webClient(String baseurl) {
        return WebClient.builder()
            .baseUrl(baseurl)
            .build();
    }
    @Bean
    String baseUrl(){
        return "http://api.stackexchange.com/2.3";
    }
    @Bean
    StackOverflowClient stackOverflowClient() {
        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient("http://api.stackexchange.com/2.3")))
                .build();
        return httpServiceProxyFactory.createClient(StackOverflowClient.class);
    }

    @Bean
    GitHubClient gitHubClient() {
        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient("https://api.github.com")))
                .build();
        return httpServiceProxyFactory.createClient(GitHubClient.class);
    }
}
