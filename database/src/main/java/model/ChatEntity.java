package model;

import java.util.List;
import lombok.Data;

@Data
public record ChatEntity(
    long id,
    List<LinkEntity> links
) {
}
