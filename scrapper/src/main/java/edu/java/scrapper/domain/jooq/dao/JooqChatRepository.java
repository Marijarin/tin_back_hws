package edu.java.scrapper.domain.jooq.dao;

import edu.java.domain.model.ChatDao;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.scrapper.domain.jooq.Tables.ASSIGNMENT;
import static edu.java.scrapper.domain.jooq.Tables.LINK;
import static edu.java.scrapper.domain.jooq.tables.Chat.CHAT;

@Repository
public class JooqChatRepository {
    private final DSLContext jooq;

    @Autowired
    public JooqChatRepository(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional
    public void addChat(long tgChatId) {
        jooq.insertInto(CHAT)
            .set(CHAT.ID, tgChatId)
            .set(CHAT.CREATED_AT, OffsetDateTime.now())
            .execute();
    }

    @Transactional
    public void deleteChat(long tgChatId) {
        jooq.deleteFrom(ASSIGNMENT)
            .where(ASSIGNMENT.CHAT_ID.eq(tgChatId))
            .execute();
        jooq.deleteFrom(CHAT)
            .where(CHAT.ID.eq(tgChatId))
            .execute();
    }

    @Transactional
    public ChatDao findChat(long tgChatId) {
        Record2<Long, OffsetDateTime> chatRecord = jooq
            .select(CHAT.ID, CHAT.CREATED_AT)
            .from(CHAT)
            .where(CHAT.ID.eq(tgChatId))
            .fetchOne();
        assert chatRecord != null;
        return new ChatDao(chatRecord.component1(), chatRecord.component2(), List.of());
    }

    @Transactional
    public List<ChatDao> findAllChatsWithLink(URI url) {
        Record1<Integer> linkId = jooq
            .select(LINK.ID)
            .from(LINK)
            .where(LINK.URL.eq(url.toString()))
            .fetchOne();
        assert linkId != null;
        var id = Long.parseLong(linkId.toString());
        Result<Record1<Long>> chatRecords = jooq
            .select(ASSIGNMENT.CHAT_ID)
            .from(ASSIGNMENT)
            .where(ASSIGNMENT.LINK_ID.eq(id))
            .fetch();
        var chatList = new ArrayList<ChatDao>();
        chatRecords.forEach(longRecord1 -> {
            long id1 = longRecord1.component1();
            var chat = findChat(id1);
            chatList.add(chat);
        });
        return chatList;
    }
}
