package edu.java.domain.jpa;

import edu.java.domain.jpa.entity.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkDao extends JpaRepository<LinkEntity, Long> {
}
