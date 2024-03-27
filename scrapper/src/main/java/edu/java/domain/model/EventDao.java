package edu.java.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class EventDao {
    @NotNull
    String description;
    @NotNull
    long linkId;
}
