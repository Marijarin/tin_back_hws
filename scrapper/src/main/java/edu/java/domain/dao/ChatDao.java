package edu.java.domain.dao;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@AllArgsConstructor
public class ChatDao {
    @Id
    long id;
    @NotNull
    OffsetDateTime createdAt;
    List<LinkDao> links;
}
