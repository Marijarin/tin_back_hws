package edu.java.dao.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import java.time.OffsetDateTime;
import java.util.List;
@Setter
@Getter
public class Chat {
    @Id
    private final long id;
    private final OffsetDateTime createdAt;
    private final Link link;

    public Chat(long id, OffsetDateTime createdAt, Link link) {
        this.id = id;
        this.createdAt = createdAt;
        this.link = link;
    }
}
