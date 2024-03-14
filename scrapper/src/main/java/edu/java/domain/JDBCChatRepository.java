package edu.java.domain;

import edu.java.domain.dao.ChatDao;
import edu.java.domain.dao.LinkDao;
import java.net.URI;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JDBCChatRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JDBCChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long addChat() {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String SQL = "insert into chat (created_at) values (?)";
        var createdTime = Timestamp.from(OffsetDateTime.now().toInstant());
        jdbcTemplate.update(SQL, createdTime);
        return Optional.ofNullable(keyHolder.getKeyAs(Long.class)).orElse(-1L);
    }

    public long deleteChat(long id) {
        String SQL = "delete from chat where id=?";
        jdbcTemplate.update(SQL);
        return id;
    }

    public List<ChatDao> findAllChatsWithLink(long linkId) {
        String SQL = "select * from chat where (select chat_id from assignment where link_id=?)";
        List<ChatDao> chatList = jdbcTemplate.query(
            SQL,
            (rs, rowNum) ->
                new ChatDao(
                    rs.getLong("id"),
                    rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC),
                    new ArrayList<>()
                ), linkId
        );
        for (ChatDao c : chatList) {
            c.getLinks().addAll(findAllLinksForChat(c.getId()));
        }
        return chatList;
    }

    public List<ChatDao> findAllChats() {
        String SQL = "select * from chat";
        return performSQLOnChats(SQL);
    }

    public List<LinkDao> findAllLinksForChat(long chatId) {
        String SQL = "select  * from link where (select link_id from assignment where chat_id=?)";
        return jdbcTemplate.query(
            SQL,
            (rs, rowNum) ->
                new LinkDao(
                    rs.getLong("id"),
                    rs.getObject("url", URI.class),
                    rs.getString("description"),
                    rs.getTimestamp("last_updated").toInstant().atOffset(ZoneOffset.UTC)
                ), chatId
        );
    }

    private List<ChatDao> performSQLOnChats(String SQL) {
        List<ChatDao> chatList = jdbcTemplate.query(
            SQL,
            (rs, rowNum) ->
                new ChatDao(
                    rs.getLong("id"),
                    rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC),
                    new ArrayList<>()
                )
        );
        for (ChatDao c : chatList) {
            c.getLinks().addAll(findAllLinksForChat(c.getId()));
        }
        return chatList;
    }
}
