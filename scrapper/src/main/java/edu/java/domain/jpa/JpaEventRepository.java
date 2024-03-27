package edu.java.domain.jpa;

import edu.java.domain.jpa.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaEventRepository extends JpaRepository<EventEntity, Long> {
}
