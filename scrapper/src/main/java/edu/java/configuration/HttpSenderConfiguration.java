package edu.java.configuration;

import edu.java.client.BotClient;
import edu.java.service.ChatService;
import edu.java.service.LinkUpdater;
import edu.java.service.LinkUpdaterScheduler;
import edu.java.service.LinkUpdaterSchedulerHttp;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "false")
public class HttpSenderConfiguration {
    @Bean
    public LinkUpdaterScheduler linkUpdaterScheduler(
        BotClient botClient,
        ChatService chatService,
        LinkUpdater linkUpdater,
        ApplicationConfig applicationConfig
    ) {
        return new LinkUpdaterSchedulerHttp(botClient, linkUpdater, chatService, applicationConfig);
    }
}
