package edu.java.domain.jpa;

import edu.java.domain.jpa.entity.ChatEntity;
import edu.java.domain.jpa.entity.LinkEntity;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkDao extends JpaRepository<LinkEntity, Long> {
    void deleteByUrl(String url);

    LinkEntity findByUrl(String url);

    List<LinkEntity>findLinkEntitiesByLastUpdatedBefore(OffsetDateTime lastUpdated);
}
