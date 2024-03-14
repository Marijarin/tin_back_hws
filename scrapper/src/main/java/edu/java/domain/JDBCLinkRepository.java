package edu.java.domain;

import edu.java.domain.dao.LinkDao;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JDBCLinkRepository {
    private final JdbcTemplate jdbcTemplate;

    public JDBCLinkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long addLink(long chatId, URI url, String description) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String SQL = "with link_insert as" +
            " (insert into link (url, description, last_updated) values (?, ?, ?) returning id)" +
            "insert into assignment(chat_id, link_id) values (? , (select id from link_insert)) ";
        var updatedTime = Timestamp.from(OffsetDateTime.now().toInstant());
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                .prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, url.toString());
            ps.setString(2, description);
            ps.setTimestamp(3, updatedTime);
            ps.setLong(4, chatId);
            return ps;
        }, keyHolder);
        return (long) Objects.requireNonNull(keyHolder.getKeys()).get("link_id");
    }

    public long deleteLink(long linkId) {
        String SQL = "delete from link where id=?";
        jdbcTemplate.update(SQL, linkId);
        return linkId;
    }

    public List<LinkDao> findAllLinksWithLastUpdateEarlierThan(OffsetDateTime lastUpdate) {
        String SQL = "select  * from link where last_updated < ?";
        return jdbcTemplate.query(SQL,
            (rs, rowNum) ->
                new LinkDao(
                    rs.getLong("id"),
                    rs.getObject("url", URI.class),
                    rs.getString("description"),
                    rs.getTimestamp("last_updated").toInstant().atOffset(ZoneOffset.UTC)
                ), lastUpdate
        );
    }

    public List<LinkDao> findAllLinks() {
        String SQL = "select  * from link";
        return jdbcTemplate.query(
            SQL,
            (rs, rowNum) ->
                new LinkDao(
                    rs.getLong("id"),
                    rs.getObject("url", URI.class),
                    rs.getString("description"),
                    rs.getTimestamp("last_updated").toInstant().atOffset(ZoneOffset.UTC)
                )
        );
    }
}
