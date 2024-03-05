package migrations.dao.model;

import java.time.OffsetDateTime;
import java.util.List;

public record ChatEntity(
    long id,
    OffsetDateTime createdAt,
    List<LinkEntity> links
) {
}
