package edu.java.service.model;

import edu.java.domain.model.EventDao;
import edu.java.domain.model.LinkDao;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class EventLink {
    @NotNull
    LinkDao link;
    @NotNull
    EventDao event;

}
