package edu.java.integration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.domain.jpa.JpaChatRepository;
import edu.java.domain.jpa.JpaEventRepository;
import edu.java.domain.jpa.JpaLinkDao;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdater;
import edu.java.service.jpa.JpaChatService;
import edu.java.service.jpa.JpaLinkService;
import edu.java.service.jpa.JpaLinkUpdater;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatusCode;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import static edu.java.integration.IntegrationTest.POSTGRES;

@Configuration
@ComponentScan("edu.java.domain.jpa")
@EnableJpaRepositories("edu.java.domain.jpa")
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class JpaTestConfig {

    @Bean("transactionManager")
    protected PlatformTransactionManager transactionManager(
        EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig dataSourceProperties = new HikariConfig();
        dataSourceProperties.setJdbcUrl(POSTGRES.getJdbcUrl());
        dataSourceProperties.setUsername(POSTGRES.getUsername());
        dataSourceProperties.setPassword(POSTGRES.getPassword());
        return new HikariDataSource(dataSourceProperties);
    }

    @Bean("entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.POSTGRESQL);
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("edu.java.domain.jpa");
        factory.setDataSource(dataSource);

        return factory;
    }

    @Bean
    public LinkService linkService(
        JpaLinkDao linkDao,
        JpaChatRepository chatRepository
    ) {
        return new JpaLinkService(linkDao, chatRepository);
    }

    @Bean
    public ChatService chatService(
        JpaChatRepository chatRepository
    ) {
        return new JpaChatService(chatRepository);
    }
    @Bean LinkUpdater linkUpdater(
        JpaLinkDao linkDao,
        JpaEventRepository eventRepository,
        GitHubClient gitHubClient,
        StackOverflowClient stackOverflowClient
    ) {
        return new JpaLinkUpdater(linkDao, eventRepository, gitHubClient, stackOverflowClient);
    }
    @Bean
    StackOverflowClient stackOverflowClient() {
        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient("http://localhost:8080")))
                .build();
        return httpServiceProxyFactory.createClient(StackOverflowClient.class);
    }

    @Bean
    GitHubClient gitHubClient() {
        HttpServiceProxyFactory httpServiceProxyFactory =
            HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient("http://localhost:8080")))
                .build();
        return httpServiceProxyFactory
            .createClient(GitHubClient.class);
    }
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
                clientResponse -> Mono.empty()
            )
            .build();
    }

}
