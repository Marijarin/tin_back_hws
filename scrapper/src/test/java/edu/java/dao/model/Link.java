package edu.java.dao.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Link {
    private final long id;
    private final String url;
    private final String description;

    public Link(long id, String url, String description) {
        this.id = id;
        this.url = url;
        this.description = description;
    }
}
