package edu.java.domain;

import edu.java.domain.dao.LinkDao;
import java.net.URI;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
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
        jdbcTemplate.update(SQL, url, description, updatedTime, chatId);
        return Optional.ofNullable(keyHolder.getKeyAs(Long.class)).orElse(-1L);
    }

    public long deleteLink(long id) {
        String SQL = "with link_delete as" +
            " (delete from link where id=? returning id)" +
            "delete from assignment where link_id=link_delete.id";
        jdbcTemplate.update(SQL, id);
        return id;
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
    public List<LinkDao> findAllLinks(){
        String SQL = "select  * from link";
        return jdbcTemplate.query(SQL,
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
