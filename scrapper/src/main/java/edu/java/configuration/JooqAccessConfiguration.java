package edu.java.configuration;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {
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
}
