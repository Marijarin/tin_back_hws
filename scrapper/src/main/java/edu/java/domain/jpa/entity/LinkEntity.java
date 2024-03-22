package edu.java.domain.jpa.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Table(name = "link")
public class LinkEntity {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "url")
    @URL
    private String url;

    @Column(name = "description")
    private String description;

    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<ChatEntity> chats = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private List<EventEntity> events = new ArrayList<>();
}
