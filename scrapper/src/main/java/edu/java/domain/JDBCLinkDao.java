package edu.java.domain;

import edu.java.domain.dao.Event;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository @SuppressWarnings({"LocalVariableName", "MultipleStringLiterals", "OperatorWrap", "MagicNumber"})
public class JDBCLinkDao {
    private final JdbcTemplate jdbcTemplate;

    public JDBCLinkDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Link addLink(long chatId, URI url, String description) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String SQL = "with link_insert as" +
            " (insert into link (url, description, last_updated) values (?, ?, ?) returning id)" +
            "insert into assignment(chat_id, link_id) values (? , (select id from link_insert)) ";
        var updatedOffset = OffsetDateTime.now();
        var updatedTime = Timestamp.from(updatedOffset.toInstant());
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, url.toString());
            ps.setString(2, description);
            ps.setTimestamp(3, updatedTime);
            ps.setLong(4, chatId);
            return ps;
        }, keyHolder);
        return new Link(
            (long) Objects.requireNonNull(keyHolder.getKeys()).get("link_id"),
            url,
            description,
            updatedOffset
        );
    }

    public Link addLink(long chatId, URI url) {
        var description = url.getHost();
        return addLink(chatId, url, description);
    }

    public Link updateLink(Link link, OffsetDateTime lastUpdate) {
        String SQL = "update link set last_updated = ? where id = ? returning (select id from link where id = ?)";
        var id =
            jdbcTemplate.queryForObject(SQL, (rs, rowNum) -> rs.getLong("id"), lastUpdate, link.getId(), link.getId());
        String SQL1 = "select * from link where id=?";
        return jdbcTemplate.queryForObject(SQL1, (rs, rowNum) -> new Link(
            rs.getLong("id"),
            URI.create(rs.getString("url")),
            rs.getString("description"),
            rs.getTimestamp("last_updated").toInstant().atOffset(ZoneOffset.UTC)
        ), id);
    }

    public Link deleteLink(long chatId, URI url) {
        String SQL1 = "select * from link where url = ?";
        var link = jdbcTemplate.queryForObject(SQL1, (rs, rowNum) -> new Link(
            rs.getLong("id"),
            URI.create(rs.getString("url")),
            rs.getString("description"),
            rs.getTimestamp("last_updated").toInstant().atOffset(ZoneOffset.UTC)
        ), url.toString());
        String SQL =
            "with d as (delete from public.assignment where assignment.link_id = ?) delete from link where url=?";
        assert link != null;
        jdbcTemplate.update(SQL, link.getId(), url.toString());
        return link;
    }

    public List<Link> findAllLinksWithLastUpdateEarlierThan(OffsetDateTime lastUpdate) {
        String SQL = "select  * from link where last_updated < ?";
        return jdbcTemplate.query(SQL, (rs, rowNum) -> new Link(
            rs.getLong("id"),
            URI.create(rs.getString("url")),
            rs.getString("description"),
            rs.getTimestamp("last_updated").toInstant().atOffset(ZoneOffset.UTC)
        ), lastUpdate);
    }

    public List<Link> findAllLinks() {
        String SQL = "select  * from link";
        return jdbcTemplate.query(SQL, (rs, rowNum) -> new Link(
            rs.getLong("id"),
            URI.create(rs.getString("url")),
            rs.getString("description"),
            rs.getTimestamp("last_updated").toInstant().atOffset(ZoneOffset.UTC)
        ));
    }

    public long findByUrlAndChat(long chatId, URI url) {
        String SQL = "select link.id from link where url = ?";
        var linkId = jdbcTemplate.queryForObject(SQL, (rs, rowNum) ->
                rs.getLong("id"),
            url.toString()
        );
        String SQL2 = "select assignment.chat_id from assignment where link_id = ?";
        var chatIds = jdbcTemplate.query(SQL2, (rs, rowNum) ->
                rs.getLong("chat_id"),
            linkId
        );
        if (chatIds.contains(chatId)) {
            return chatId;
        } else {
            return -1L;
        }
    }

    public List<Link> findAllLinksFromChat(long chatId) {
        String SQL1 = "select assignment.link_id from assignment where chat_id = ?";
        List<Long> ids = jdbcTemplate.query(SQL1, (rs, rowNum) ->
                rs.getLong("link_id"),
            chatId
        );
        List<Link> links = new ArrayList<>();
        if (!ids.isEmpty()) {
            for (Long id : ids) {
                String SQL = "select  * from link where id = ? ";
                var link = jdbcTemplate.queryForObject(SQL, (rs, rowNum) -> new Link(
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

    public Event putEventType(long linkId, String description) {

        String SQL = "insert into events (event, link_id) VALUES (?, ?) returning event, link_id";
        return jdbcTemplate.queryForObject(SQL, (rs, rowNum) -> new Event(
            rs.getString("event"),
            rs.getLong("link_id")
        ), description, linkId);

    }
}
