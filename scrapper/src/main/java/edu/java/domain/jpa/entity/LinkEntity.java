package edu.java.domain.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
