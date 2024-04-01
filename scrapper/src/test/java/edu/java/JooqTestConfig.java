package edu.java;

import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.domain.jooq.JooqChatRepository;
import edu.java.domain.jooq.JooqLinkDao;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdater;
import edu.java.service.jooq.JooqChatService;
import edu.java.service.jooq.JooqLinkService;
import edu.java.service.jooq.JooqLinkUpdater;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatusCode;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import javax.sql.DataSource;
import static edu.java.IntegrationTest.POSTGRES;

@Configuration
@ComponentScan("edu.java.domain.jooq")
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class JooqTestConfig {
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
    @Bean
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create()
            .url(POSTGRES.getJdbcUrl())
            .username(POSTGRES.getUsername())
            .password(POSTGRES.getPassword())
            .build();
    }
    @Bean
    public DefaultConfigurationCustomizer postgresJooqCustomizer() {
        return (DefaultConfiguration c) -> c.settings()
            .withRenderSchema(false)
            .withRenderFormatted(true)
            .withRenderQuotedNames(RenderQuotedNames.NEVER);
    }

    @Bean
    public LinkService linkService(
        JooqLinkDao linkDao
    ) {
        return new JooqLinkService(linkDao);
    }

    @Bean
    public ChatService chatService(
        JooqChatRepository chatRepository
    ) {
        return new JooqChatService(chatRepository);
    }

    @Bean LinkUpdater linkUpdater(
        JooqLinkDao linkDao,
        GitHubClient gitHubClient,
        StackOverflowClient stackOverflowClient
    ) {
        return new JooqLinkUpdater(linkDao, gitHubClient, stackOverflowClient);
    }
    @Bean
    StackOverflowClient stackOverflowClient() {
        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient()))
                .build();
        return httpServiceProxyFactory.createClient(StackOverflowClient.class);
    }

    @Bean
    GitHubClient gitHubClient() {
        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient()))
                .build();
        return httpServiceProxyFactory
            .createClient(GitHubClient.class);
    }
    @Bean
    @Scope("prototype")
    WebClient webClient() {
        return WebClient.builder()
            .baseUrl("http://localhost:8080")
            .exchangeStrategies(ExchangeStrategies
                .builder()
                .codecs(codecs -> codecs
                    .defaultCodecs()
                    .maxInMemorySize(500 * 1024))
                .build())
            .defaultStatusHandler(
                HttpStatusCode::isError,
                clientResponse -> Mono.empty()
            )
            .build();
    }
}
