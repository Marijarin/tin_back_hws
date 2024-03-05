package migrations.dao.model;

import lombok.Data;
import java.util.List;

public record LinkEntity(
    long id,
    String url,
    String description
) {

}
