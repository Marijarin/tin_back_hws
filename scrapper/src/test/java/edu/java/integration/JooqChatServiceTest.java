package edu.java.integration;

import edu.java.service.jooq.JooqChatService;
import edu.java.service.jooq.JooqLinkService;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {JooqTestConfig.class, JooqAutoConfiguration.class})
@Sql(value = "classpath:sql/put_chat.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class JooqChatServiceTest extends IntegrationTest {
    private final JooqChatService chatService;
    private final JooqLinkService linkService;

    @Autowired
    public JooqChatServiceTest(JooqChatService chatService, JooqLinkService linkService) {
        this.chatService = chatService;
        this.linkService = linkService;
    }

    @Test
    @Transactional
    @Rollback
    void findsChat() {
        long chatId = 10000L;
        var result = chatService.findChatById(chatId);
        assertThat(result.getId()).isEqualTo(chatId);
    }

    @Test
    @Transactional
    @Rollback
    void addsChat() {
        long chatId = 10001L;
        chatService.register(chatId);
        var result = chatService.findChatById(chatId);
        assertThat(result.getId()).isEqualTo(chatId);
    }

    @Test
    @Transactional
    @Rollback
    void removesChat() {
        long chatId = 10000L;
        chatService.unregister(chatId);

        var result = chatService.findChatById(chatId);

        assertThat(result.getId()).isEqualTo(0L);
    }

    @Test
    @Transactional
    @Rollback
    void findsAllLinksOfChat() {
        long chatId = 10000L;
        var uri = URI.create(
            "https://stackoverflow.com/questions/68547843/bean-named-userservice-is-expected-to-be-of-type-demo-spring-orm-userservice");
        linkService.add(chatId, uri);

        var result = chatService.findAllChatsWithLink(uri);

        assertThat(result.getFirst().getId()).isEqualTo(chatId);
    }
}
