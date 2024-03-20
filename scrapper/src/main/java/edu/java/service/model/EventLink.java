package edu.java.service.model;

import edu.java.domain.dao.Event;
import edu.java.domain.dao.Link;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class EventLink {
    @NotNull
    Link link;
    @NotNull
    Event event;

}
