package model;

import lombok.Data;
import java.util.List;
@Data
public record LinkEntity(
    long id,
    String url,
    String description
) {

}
