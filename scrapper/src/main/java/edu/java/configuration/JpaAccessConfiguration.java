package edu.java.configuration;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {
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
}
