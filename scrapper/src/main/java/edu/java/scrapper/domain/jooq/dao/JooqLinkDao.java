package edu.java.scrapper.domain.jooq.dao;

import edu.java.domain.model.EventDao;
import edu.java.domain.model.LinkDao;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Result;
import org.springframework.stereotype.Repository;
import static edu.java.scrapper.domain.jooq.Tables.ASSIGNMENT;
import static edu.java.scrapper.domain.jooq.Tables.EVENTS;
import static edu.java.scrapper.domain.jooq.Tables.LINK;

@Repository
public class JooqLinkDao {
    private final DSLContext jooq;

    public JooqLinkDao(DSLContext jooq) {
        this.jooq = jooq;
    }

    public LinkDao addLink(long tgChatId, URI url) {
        jooq.insertInto(LINK)
            .set(LINK.URL, url.toString())
            .set(LINK.LAST_UPDATED, OffsetDateTime.now())
            .set(LINK.DESCRIPTION, url.getHost())
            .execute();
        var link = findByUrl(url);
        jooq.insertInto(ASSIGNMENT)
            .set(ASSIGNMENT.LINK_ID, link.getId())
            .set(ASSIGNMENT.CHAT_ID, tgChatId)
            .execute();
        return link;
    }

    public LinkDao deleteLink(long tgChatId, URI url) {
        var link = findByUrl(url);
        jooq.deleteFrom(ASSIGNMENT)
            .where(ASSIGNMENT.LINK_ID.eq(link.getId()))
            .execute();
        jooq.deleteFrom(LINK)
            .where(LINK.URL.eq(url.toString()))
            .execute();
        return link;
    }

    public Collection<LinkDao> findAllLinksFromChat(long tgChatId) {
        Result<Record1<Long>> linkIds = jooq
            .select(ASSIGNMENT.LINK_ID)
            .from(ASSIGNMENT)
            .where(ASSIGNMENT.CHAT_ID.eq(tgChatId))
            .fetch();
        var links = new ArrayList<LinkDao>();
        linkIds.forEach(longRecord1 -> {
            Record4<Integer, String, String, OffsetDateTime> link = jooq
                .select(LINK.ID, LINK.URL, LINK.DESCRIPTION, LINK.LAST_UPDATED)
                .from(LINK)
                .where(LINK.ID.eq(Math.toIntExact(longRecord1.component1())))
                .fetchOne();
            assert link != null;
            links.add(new LinkDao(
                longRecord1.component1(),
                URI.create(link.component2()),
                link.component3(),
                link.component4()
            ));
        });
        return links;
    }

    public List<LinkDao> findAllLinksWithLastUpdateEarlierThan(OffsetDateTime lastUpdate) {
        Result<Record4<Integer, String, String, OffsetDateTime>> linkRecords = jooq
            .select(LINK.ID, LINK.URL, LINK.DESCRIPTION, LINK.LAST_UPDATED)
            .from(LINK)
            .where(LINK.LAST_UPDATED.lessThan(lastUpdate))
            .fetch();
        var links = new ArrayList<LinkDao>();
        linkRecords.forEach(link -> links.add(new LinkDao(
            link.component1(),
            URI.create(link.component2()),
            link.component3(),
            link.component4()
        )));
        return links;
    }

    public EventDao putEventType(long linkId, String description) {
        jooq.insertInto(EVENTS)
            .set(EVENTS.EVENT, description)
            .set(EVENTS.LINK_ID, linkId)
            .execute();
        return new EventDao(description, linkId);
    }

    public LinkDao updateLink(LinkDao link, OffsetDateTime lastUpdate) {
        jooq.update(LINK)
            .set(LINK.LAST_UPDATED, lastUpdate)
            .where(LINK.ID.eq((int) link.getId()))
            .execute();
        return findByUrl(link.getUri());
    }

    public LinkDao findByUrl(URI url) {
        Record4<Integer, String, String, OffsetDateTime> link = jooq
            .select(LINK.ID, LINK.URL, LINK.DESCRIPTION, LINK.LAST_UPDATED)
            .from(LINK)
            .where(LINK.URL.eq(url.toString()))
            .fetchOne();
        assert link != null;
        var id = Long.parseLong(link.component1().toString());
        return new LinkDao(id, URI.create(link.component2()), link.component3(), link.component4());
    }
}
