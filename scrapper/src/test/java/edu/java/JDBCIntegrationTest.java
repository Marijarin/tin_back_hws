package edu.java;

import edu.java.domain.JDBCChatRepository;
import edu.java.domain.JDBCLinkDao;
import edu.java.domain.dao.Chat;
import edu.java.domain.dao.Link;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {TestConfig.class, JDBCChatRepository.class, JDBCLinkDao.class})
@Testcontainers
public class JDBCIntegrationTest extends IntegrationTest {
    @Autowired
    JDBCLinkDao linkRepository;
    @Autowired
    JDBCChatRepository chatRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
//    private TransactionTemplate transactionTemplate;
//
//    @BeforeEach
//    void setUp() {
//        transactionTemplate = new TransactionTemplate(transactionManager);
//    }

    @Test
    @Transactional
    @Rollback
    void addChat() {
        var resultChat = chatRepository.addChat(1L);
        var resultChat1 = chatRepository.addChat(2L);
        var resultChat2 = chatRepository.addChat(3L);

        assertThat(resultChat).isGreaterThanOrEqualTo(1L);
        assertThat(resultChat1).isGreaterThan(resultChat);
        assertThat(resultChat2).isGreaterThan(resultChat1);
    }

    @Test
    @Transactional
    @Rollback
    void removeChat() {
        // transactionTemplate.execute(status -> {
        var resultChat = chatRepository.addChat(1L);
        chatRepository.deleteChat(resultChat);

        assertThatThrownBy(() ->
            chatRepository.findChat(resultChat)).isInstanceOf(EmptyResultDataAccessException.class);
        //             return null;
        //           }
//        );
    }

    @Test
    @Transactional
    @Rollback
    void addLink() {
        var resultChat = chatRepository.addChat(1L);
        var resultLink = linkRepository
            .addLink(
                resultChat,
                URI.create(
                    "https://stackoverflow.com/questions/64268012/java-postgresql-how-to-generate-primary-key-automatically"),
                "primary key"
            );

        assertThat(resultLink.getId()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @Transactional
    @Rollback
    void removeLink() {
        //       transactionTemplate.execute(status -> {
        var resultChat = chatRepository.addChat(1L);
        var link = new Link(
            resultChat,
            URI.create(
                "https://stackoverflow.com/questions/64268012/java-postgresql-how-to-generate-primary-key-automatically"),
            "primary key"
        );
        var resultLink = linkRepository
            .addLink(link.getId(), link.getUri(), link.getDescription());
        linkRepository.deleteLink(resultChat, resultLink.getUri());
        var findLink = linkRepository.findAllLinks();

        assertThat(findLink).isEmpty();
        //               return 0;
        //           }
        //       );
    }

    @Test
    @Transactional
    @Rollback
    void findAllChats() {
        //      transactionTemplate.execute(status -> {
        var resultChatList = new ArrayList<Chat>();
        for (int i = 0; i < 4; i++) {
            var id = chatRepository.addChat(i);
            var chat = chatRepository.findChat(id);
            resultChatList.add(chat);
        }
        var resultList = chatRepository.findAllChats();

        assertThat(resultChatList.size()).isEqualTo(resultList.size());
//                return 0;
//            }
//        );
    }

    @Test
    @Transactional
    @Rollback
    void findAllChatsWithLink() {
        //       transactionTemplate.execute(status -> {
        var resultChatList = new ArrayList<Chat>();
        for (int i = 1; i < 4; i++) {
            var id = chatRepository.addChat(i);
            var chat = chatRepository.findChat(id);
            resultChatList.add(chat);
        }
        var link = linkRepository.addLink(
            resultChatList.getFirst().getId(),
            URI.create(
                "https://stackoverflow.com/questions/64268012/java-postgresql-how-to-generate-primary-key-automatically"),
            "primary key"
        );
        var resultList = chatRepository.findAllChatsWithLink(link.getId());

        assertThat(resultChatList.size()).isEqualTo(3);
        assertThat(resultList.size()).isEqualTo(1);
        assertThat(resultChatList.getFirst().getLinks()).isEmpty();
        assertThat(resultList.getFirst().getLinks()).isNotEmpty();
//                return 0;
//            }
//        );
    }

    @Test
    @Transactional
    @Rollback
    void addLinkWithoutDescription() {
        var resultChat = chatRepository.addChat(1L);
        var uri = URI.create(
            "https://stackoverflow.com/questions/64268012/java-postgresql-how-to-generate-primary-key-automatically");
        var resultLink = linkRepository.addLink(resultChat, uri);

        assertThat(resultLink.getDescription()).isEqualTo(uri.getHost());
        assertThat(resultLink.getUri()).isEqualTo(uri);
    }

    @Test
    @Transactional
    @Rollback
    void findAllLinksFromChat(){
        for (int i = 1; i < 4; i++) {
            chatRepository.addChat(i);
        }
        var uri1 = URI.create(
            "https://stackoverflow.com/questions/64268012/java-postgresql-how-to-generate-primary-key-automatically");
        var uri2 = URI.create(
            "https://github.com/ivannikolaev/java_h2/blob/main/s6_jpa/simple/src/main/java/com/example/simple/PostService.java");

        linkRepository.addLink(1L, uri2);
        linkRepository.addLink(2L, uri1);

        var result1 = linkRepository.findAllLinksFromChat(1L).stream().map(Link::getUri);
        var result2 = linkRepository.findAllLinksFromChat(2L).stream().map(Link::getUri);

        assertThat(result1).isEqualTo(List.of(uri2));
        assertThat(result2).isEqualTo(List.of(uri1));
    }
}