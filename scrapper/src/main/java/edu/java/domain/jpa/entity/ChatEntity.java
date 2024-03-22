package edu.java.domain.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "chat")
public class ChatEntity {
    @Id
    private Long id;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<LinkEntity> links;

}
