package edu.java.dao.model;

import org.springframework.data.annotation.Id;
import java.time.OffsetDateTime;
import java.util.List;

public record Chat(
    @Id
    long id,
    String createdAt,
    Link link
) {
}
