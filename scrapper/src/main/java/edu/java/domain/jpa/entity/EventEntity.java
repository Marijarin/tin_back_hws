package edu.java.domain.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "event", schema = "public")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_id_seq")
    @SequenceGenerator(name = "event_id_seq", sequenceName = "event_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "type")
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    private LinkEntity link;

}
