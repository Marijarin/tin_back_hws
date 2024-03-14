package edu.java.domain;

import edu.java.domain.dao.ChatDao;
import edu.java.domain.dao.LinkDao;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
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
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                .prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, createdTime);
            return ps;
        }, keyHolder);
        int id = (int) Objects.requireNonNull(keyHolder.getKeys()).get("id");
        return id;
    }

    public long deleteChat(long id) {
        String SQL = "delete from chat where id=?";
        jdbcTemplate.update(SQL, id);
        return id;
    }

    public List<ChatDao> findAllChatsWithLink(long linkId) {
        String SQL = "select * from chat where id = (select chat_id from assignment where link_id=?)";
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
        List<ChatDao> chatList = jdbcTemplate.query(
            SQL,
            (rs, rowNum) ->
                new ChatDao(
                    rs.getLong("id"),
                    rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC),
                    new ArrayList<>()
                )
        );
        if(!chatList.isEmpty()) {
            for (ChatDao c : chatList) {
                c.getLinks().addAll(findAllLinksForChat(c.getId()));
            }
        }
        return chatList;
    }
    public void deleteAll(){
        String SQL = "delete from chat";
        jdbcTemplate.update(SQL);
    }

    public ChatDao findChat(long id){
        String SQL = "select * from chat where id = ?";
       var chat =  jdbcTemplate.queryForObject(
            SQL,
            (rs, rowNum) ->
                new ChatDao(
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

    public List<LinkDao> findAllLinksForChat(long chatId) {
        String SQL = "select  * from link where id = (select link_id from assignment where chat_id=?)";
        return jdbcTemplate.query(
            SQL,
            (rs, rowNum) ->
                new LinkDao(
                    rs.getLong("id"),
                    URI.create(rs.getString("url")),
                    rs.getString("description"),
                    rs.getTimestamp("last_updated").toInstant().atOffset(ZoneOffset.UTC)
                ), chatId
        );
    }
}
