package edu.java.domain.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "event")
public class EventEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "type")
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    private LinkEntity link;

}
