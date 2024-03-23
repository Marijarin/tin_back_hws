package edu.java.domain.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "link", schema = "public")
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "link_id_seq")
    @SequenceGenerator(name = "link_id_seq", sequenceName = "link_id_seq", allocationSize = 1)
    private long id;

    @Column(name = "url")
    private String url;

    @Column(name = "description")
    private String description;

    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated;

    @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(name = "assignment",
               joinColumns = {@JoinColumn(name = "link_id", referencedColumnName = "id")},
               inverseJoinColumns = {@JoinColumn(name = "chat_id", referencedColumnName = "id")})
    private List<ChatEntity> chats = new ArrayList<>();

    @OneToMany(mappedBy = "link", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EventEntity> events = new ArrayList<>();
}
