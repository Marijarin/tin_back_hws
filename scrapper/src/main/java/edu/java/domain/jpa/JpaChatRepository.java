package edu.java.domain.jpa;

import edu.java.domain.jpa.entity.ChatEntity;
import edu.java.domain.jpa.entity.LinkEntity;
import java.util.Collection;
import java.util.List;
import edu.java.scrapper.domain.jooq.tables.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatRepository extends JpaRepository<ChatEntity, Long> {
    void deleteChatEntityById(long id);

    ChatEntity findChatEntityById(long id);
}
