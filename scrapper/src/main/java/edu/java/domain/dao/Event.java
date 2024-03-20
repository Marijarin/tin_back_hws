package edu.java.domain.dao;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Event {
    @NotNull
    String description;
    @NotNull
    long linkId;
}
