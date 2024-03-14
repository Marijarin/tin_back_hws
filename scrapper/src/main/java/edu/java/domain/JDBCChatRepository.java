package edu.java.domain;

import edu.java.configuration.ApplicationConfig;
import edu.java.domain.dao.ChatDao;
import edu.java.domain.dao.LinkDao;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JDBCChatRepository {



    private final JdbcTemplate jdbcTemplate;

    private Logger logger = LogManager.getLogger();

    @Autowired
    public JDBCChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long addChat() {
        try {
            String SQL = "insert into chat (created_at) values (?)";
            PreparedStatement statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            statement.setTimestamp(1, Timestamp.from(OffsetDateTime.now().toInstant()));
            statement.executeUpdate(SQL);
            ResultSet resultSet = statement.getGeneratedKeys();
            return resultSet.getLong("id");
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return -1L;
        }
    }

    public long deleteChat(long id) {
        try {
            String SQL = "delete from chat where id=?";
            var statement = connection.prepareStatement(SQL);
            statement.setLong(1, id);
            statement.executeUpdate(SQL);
            ResultSet resultSet = statement.getResultSet();
            return resultSet.getLong("id");
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return -1L;
        }
    }

    public List<ChatDao> findAllChatsWithLink(long linkId) {
        List<ChatDao> chatList = new ArrayList<>();
        try {
            String SQL = "select * from chat where (select chat_id from assignment where link_id=?)";
            var statement = connection.prepareStatement(SQL);
            statement.setLong(1, linkId);
            statement.executeUpdate(SQL);
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                long id = resultSet.getLong("chat_id");
                OffsetDateTime createdAt = resultSet.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC);
                var links = findAllLinksForChat(id);
                chatList.add(new ChatDao(id, createdAt, links));
            }
            return chatList;
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return List.of();
        }

    }

    public List<ChatDao> findAllChats() {
        List<ChatDao> chatList = new ArrayList<>();
        try {
            String SQL = "select * from chat ";
            var statement = connection.prepareStatement(SQL);
            statement.executeUpdate(SQL);
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                OffsetDateTime createdAt = resultSet.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC);
                chatList.add(new ChatDao(id, createdAt, new ArrayList<>()));
            }
            return chatList;
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return List.of();
        }
    }

    public long addLink(long chatId, URI url, String description) {
        try {
            String SQL = "insert into link (url, description, last_updated) values (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            statement.setURL(1, url.toURL());
            statement.setString(2, description);
            statement.setTimestamp(3, Timestamp.from(Instant.now()));
            ResultSet resultSet = statement.getGeneratedKeys();
            var linkId = resultSet.getLong("id");
            String SQL2 = "insert into assignment(chat_id, link_id) VALUES (?,?)";
            PreparedStatement statementChat = connection.prepareStatement(SQL2);
            statementChat.setLong(1, chatId);
            statementChat.setLong(2, linkId);
            statementChat.executeUpdate(SQL2);
            return linkId;
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return -1L;
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
            return -2L;
        }
    }

    public long deleteLink(long id) {
        try {
            String SQL = "delete from link where id=?";
            var statement = connection.prepareStatement(SQL);
            statement.setLong(1, id);
            statement.executeUpdate(SQL);
            ResultSet resultSet = statement.getResultSet();
            return resultSet.getLong("id");
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return -1L;
        }
    }

    public List<LinkDao> findAllLinksWithLastUpdateEarlierThan(OffsetDateTime lastUpdate) {

    }

    public List<LinkDao> findAllLinksForChat(long chatId) {
        List<LinkDao> links = new ArrayList<>();
        try {
            String SQL = "select  * from link where (select link_id from assignment where chat_id=?)";
            var statement = connection.prepareStatement(SQL);
            statement.setLong(1, chatId);
            statement.executeUpdate(SQL);
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                URI url = resultSet.getURL("url").toURI();
                String description = resultSet.getString("description");
                OffsetDateTime lastUpdated =
                    resultSet.getTimestamp("last_updated").toInstant().atOffset(ZoneOffset.UTC);
                links.add(new LinkDao(id, url, description, lastUpdated));
            }
            return links;
        } catch (SQLException | URISyntaxException e) {
            logger.error(e.getMessage());
            return List.of();
        }
    }
}
