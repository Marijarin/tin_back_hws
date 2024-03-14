package edu.java;

import edu.java.domain.JDBCChatRepository;
import edu.java.domain.JDBCLinkRepository;
import edu.java.domain.dao.ChatDao;
import edu.java.domain.dao.LinkDao;
import java.net.URI;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {TestConfig.class, JDBCChatRepository.class, JDBCLinkRepository.class})
@Testcontainers
public class JDBCIntegrationTest extends IntegrationTest {
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    JDBCLinkRepository linkRepository;
    @Autowired
    JDBCChatRepository chatRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    @Transactional
    @Rollback
    void addChat() {
        var resultChat = chatRepository.addChat();
        var resultChat1 = chatRepository.addChat();
        var resultChat2 = chatRepository.addChat();

        assertThat(resultChat).isGreaterThanOrEqualTo(1);
        assertThat(resultChat1).isGreaterThan(resultChat);
        assertThat(resultChat2).isGreaterThan(resultChat1);
    }

    @Test
    @Transactional
    @Rollback
    void removeChat() {
        transactionTemplate.execute(status -> {
                var resultChat = chatRepository.addChat();
                chatRepository.deleteChat(resultChat);

                assertThatThrownBy(() ->
                    chatRepository.findChat(resultChat)).isInstanceOf(EmptyResultDataAccessException.class);
                return null;
            }
        );
    }

    @Test
    @Transactional
    @Rollback
    void addLink() {
        var resultChat = chatRepository.addChat();
        var resultLink = linkRepository
            .addLink(
                resultChat,
                URI.create(
                    "https://stackoverflow.com/questions/64268012/java-postgresql-how-to-generate-primary-key-automatically"),
                "primary key"
            );

        assertThat(resultLink).isGreaterThanOrEqualTo(1);
    }

    @Test
    @Transactional
    @Rollback
    void removeLink() {
        transactionTemplate.execute(status -> {
                var resultChat = chatRepository.addChat();
                var link = new LinkDao(
                    resultChat,
                    URI.create(
                        "https://stackoverflow.com/questions/64268012/java-postgresql-how-to-generate-primary-key-automatically"),
                    "primary key"
                );
                var resultLink = linkRepository
                    .addLink(link.getId(), link.getUri(), link.getDescription());
                linkRepository.deleteLink(resultLink);
                var findLink = linkRepository.findAllLinks();

                assertThat(findLink).isEmpty();
                return 0;
            }
        );
    }

    @Test
    @Transactional
    @Rollback
    void findAllChats() {
        transactionTemplate.execute(status -> {
                chatRepository.deleteAll();
                var resultChatList = new ArrayList<ChatDao>();
                for (int i = 0; i < 4; i++) {
                    var id = chatRepository.addChat();
                    var chat = chatRepository.findChat(id);
                    resultChatList.add(chat);
                }
                var resultList = chatRepository.findAllChats();

                assertThat(resultChatList.size()).isEqualTo(resultList.size());
                return 0;
            }
        );
    }

    @Test
    @Transactional
    @Rollback
    void findAllChatsWithLink() {
        transactionTemplate.execute(status -> {
                var resultChatList = new ArrayList<ChatDao>();
                for (int i = 0; i < 3; i++) {
                    var id = chatRepository.addChat();
                    var chat = chatRepository.findChat(id);
                    resultChatList.add(chat);
                }
                var id = linkRepository.addLink(
                    resultChatList.getFirst().getId(),
                    URI.create(
                        "https://stackoverflow.com/questions/64268012/java-postgresql-how-to-generate-primary-key-automatically"),
                    "primary key"
                );
                var resultList = chatRepository.findAllChatsWithLink(id);

                assertThat(resultChatList.size()).isEqualTo(3);
                assertThat(resultList.size()).isEqualTo(1);
                assertThat(resultChatList.getFirst().getLinks()).isEmpty();
                assertThat(resultList.getFirst().getLinks()).isNotEmpty();
                return 0;
            }
        );
    }

}
