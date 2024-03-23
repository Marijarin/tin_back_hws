package edu.java.domain.jpa.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "chat", schema = "public")
public class ChatEntity {
    @Id
    private Long id;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "assignment",
            joinColumns = {@JoinColumn(name = "chat_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "link_id", referencedColumnName = "id")})
    private List<LinkEntity> links = new ArrayList<>();

}
