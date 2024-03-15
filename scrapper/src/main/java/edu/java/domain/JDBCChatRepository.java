package edu.java.domain;

import edu.java.domain.dao.Chat;
import edu.java.domain.dao.Link;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public long addChat(long chatId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String SQL = "insert into chat (id, created_at) values (?, ?)";
        var createdTime = Timestamp.from(OffsetDateTime.now().toInstant());
        jdbcTemplate.update(SQL, chatId, createdTime);
        return chatId;
    }

    public long deleteChat(long id) {
        String SQL = "delete from chat where id=?";
        jdbcTemplate.update(SQL, id);
        return id;
    }

    public List<Chat> findAllChatsWithLink(long linkId) {
        String SQL = "select * from chat where id = (select chat_id from assignment where link_id=?)";
        List<Chat> chatList = jdbcTemplate.query(
            SQL,
            (rs, rowNum) ->
                new Chat(
                    rs.getLong("id"),
                    rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC),
                    new ArrayList<>()
                ), linkId
        );
        for (Chat c : chatList) {
            c.getLinks().addAll(findAllLinksForChat(c.getId()));
        }
        return chatList;
    }

    public List<Chat> findAllChats() {
        String SQL = "select * from chat";
        List<Chat> chatList = jdbcTemplate.query(
            SQL,
            (rs, rowNum) ->
                new Chat(
                    rs.getLong("id"),
                    rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC),
                    new ArrayList<>()
                )
        );
        if(!chatList.isEmpty()) {
            for (Chat c : chatList) {
                c.getLinks().addAll(findAllLinksForChat(c.getId()));
            }
        }
        return chatList;
    }
    public void deleteAll(){
        String SQL = "delete from chat";
        jdbcTemplate.update(SQL);
    }

    public Chat findChat(long id){
        String SQL = "select * from chat where id = ?";
       var chat =  jdbcTemplate.queryForObject(
            SQL,
            (rs, rowNum) ->
                new Chat(
                    rs.getLong("id"),
                    rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC),
                    new ArrayList<>()
                ), id
        );
        if (chat != null) {
            return chat;
        }
        throw new NullPointerException();
    }

    public List<Link> findAllLinksForChat(long chatId) {
        String SQL = "select  * from link where id = (select link_id from assignment where chat_id=?)";
        return jdbcTemplate.query(
            SQL,
            (rs, rowNum) ->
                new Link(
                    rs.getLong("id"),
                    URI.create(rs.getString("url")),
                    rs.getString("description"),
                    rs.getTimestamp("last_updated").toInstant().atOffset(ZoneOffset.UTC)
                ), chatId
        );
    }
}
