package edu.java.domain.model;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Setter
@Getter
@AllArgsConstructor
public class LinkDao {
    @Id
    long id;
    @NotNull
    URI uri;
    String description;
    @NotNull
    OffsetDateTime lastUpdated;

    public LinkDao(long id, URI uri, String description) {
        this.id = id;
        this.uri = uri;
        this.description = description;
        this.lastUpdated = OffsetDateTime.now();
    }
}
