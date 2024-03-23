package edu.java.configuration;

import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.domain.jdbc.JdbcChatRepository;
import edu.java.domain.jdbc.JdbcLinkDao;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdater;
import edu.java.service.jdbc.JdbcChatService;
import edu.java.service.jdbc.JdbcLinkService;
import edu.java.service.jdbc.JdbcLinkUpdater;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {
    @Bean
    public LinkService linkService(
        JdbcLinkDao linkDao
    ) {
        return new JdbcLinkService(linkDao);
    }

    @Bean
    public ChatService chatService(
        JdbcChatRepository chatRepository
    ) {
        return new JdbcChatService(chatRepository);
    }

    @Bean LinkUpdater linkUpdater(
        JdbcLinkDao linkDao,
        GitHubClient gitHubClient,
        StackOverflowClient stackOverflowClient
    ) {
        return new JdbcLinkUpdater(linkDao, gitHubClient, stackOverflowClient);
    }
}
