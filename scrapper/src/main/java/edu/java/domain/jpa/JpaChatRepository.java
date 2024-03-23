package edu.java.domain.jpa;

import edu.java.domain.jpa.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatRepository extends JpaRepository<ChatEntity, Long> {
    void deleteChatEntityById(long id);

    ChatEntity findChatEntityById(long id);
}
