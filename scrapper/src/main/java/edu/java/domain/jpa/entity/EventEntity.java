package edu.java.domain.jpa.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Table(name = "event")
public class EventEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "type")
    private String type;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<LinkEntity> links = new ArrayList<>();

}
