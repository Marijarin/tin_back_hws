package edu.java.domain.jdbc;

import edu.java.domain.model.ChatDao;
import edu.java.domain.model.LinkDao;
import java.net.URI;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings({"LocalVariableName", "MultipleStringLiterals"})
public class JDBCChatRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JDBCChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long addChat(long chatId) {
        String SQL = "insert into chat (id, created_at) values (?, ?)";
        var createdTime = Timestamp.from(OffsetDateTime.now().toInstant());
        jdbcTemplate.update(SQL, chatId, createdTime);
        return chatId;
    }

    public long deleteChat(long id) {
        String SQL =
            "with d as (delete from public.assignment where assignment.chat_id = ?) delete from chat where id=?";
        jdbcTemplate.update(SQL, id, id);
        return id;
    }

    public List<ChatDao> findAllChatsWithLink(long linkId) {
        String SQL1 = "select assignment.chat_id from assignment where link_id = ?";
        Long id = jdbcTemplate.queryForObject(SQL1, (rs, rowNum) ->
                rs.getLong("chat_id"),
            linkId
        );
        List<ChatDao> chatList = List.of();
        if (id != null) {
            String SQL = "select * from chat where id = ?";
            chatList = jdbcTemplate.query(
                SQL,
                (rs, rowNum) ->
                    new ChatDao(
                        rs.getLong("id"),
                        rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC),
                        new ArrayList<>()
                    ), id
            );
            for (ChatDao c : chatList) {
                c.getLinks().addAll(findAllLinksForChat(c.getId()));
            }
        }
        return chatList;
    }

    @SuppressWarnings("LineLength")
    public List<ChatDao> findAllChatsWithLink(URI url) {
        String SQL1 = "select id from link where url = ?";
        Long id = jdbcTemplate.queryForObject(SQL1, (rs, rowNum) ->
                rs.getLong("id"),
            url.toString()
        );
        if (id != null) {
            return findAllChatsWithLink(id);
        }
        return List.of();
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
        if (!chatList.isEmpty()) {
            for (ChatDao c : chatList) {
                c.getLinks().addAll(findAllLinksForChat(c.getId()));
            }
        }
        return chatList;
    }

    public void deleteAll() {
        String SQL = "delete from chat";
        jdbcTemplate.update(SQL);
    }

    public ChatDao findChat(long id) {
        String SQL = "select * from chat where id = ?";
        try {
            return jdbcTemplate.queryForObject(
                SQL,
                (rs, rowNum) ->
                    new ChatDao(
                        rs.getLong("id"),
                        rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC),
                        new ArrayList<>()
                    ), id
            );
        } catch (EmptyResultDataAccessException e) {
            return new ChatDao(0L, OffsetDateTime.now(), null);
        }
    }

    public List<LinkDao> findAllLinksForChat(long chatId) {
        String SQL1 = "select assignment.link_id from assignment where chat_id = ?";
        List<Long> ids = jdbcTemplate.query(SQL1, (rs, rowNum) ->
                rs.getLong("link_id"),
            chatId
        );
        List<LinkDao> links = new ArrayList<>();
        if (!ids.isEmpty()) {
            for (Long id : ids) {
                String SQL = "select  * from link where id = ? ";
                var link = jdbcTemplate.queryForObject(SQL, (rs, rowNum) -> new LinkDao(
                    rs.getLong("id"),
                    URI.create(rs.getString("url")),
                    rs.getString("description"),
                    rs.getTimestamp("last_updated").toInstant().atOffset(ZoneOffset.UTC)
                ), id);
                links.add(link);
            }
        }
        return links;
    }
}
