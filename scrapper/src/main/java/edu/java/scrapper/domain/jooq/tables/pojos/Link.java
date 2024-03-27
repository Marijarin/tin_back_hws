/*
 * This file is generated by jOOQ.
 */
package edu.java.scrapper.domain.jooq.tables.pojos;


import jakarta.validation.constraints.Size;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.time.OffsetDateTime;

import javax.annotation.processing.Generated;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.13"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Link implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String url;
    private String description;
    private OffsetDateTime lastUpdated;

    public Link() {}

    public Link(Link value) {
        this.id = value.id;
        this.url = value.url;
        this.description = value.description;
        this.lastUpdated = value.lastUpdated;
    }

    @ConstructorProperties({ "id", "url", "description", "lastUpdated" })
    public Link(
        @Nullable Long id,
        @NotNull String url,
        @Nullable String description,
        @NotNull OffsetDateTime lastUpdated
    ) {
        this.id = id;
        this.url = url;
        this.description = description;
        this.lastUpdated = lastUpdated;
    }

    /**
     * Getter for <code>LINK.ID</code>.
     */
    @Nullable
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for <code>LINK.ID</code>.
     */
    public void setId(@Nullable Long id) {
        this.id = id;
    }

    /**
     * Getter for <code>LINK.URL</code>.
     */
    @jakarta.validation.constraints.NotNull
    @Size(max = 1000000000)
    @NotNull
    public String getUrl() {
        return this.url;
    }

    /**
     * Setter for <code>LINK.URL</code>.
     */
    public void setUrl(@NotNull String url) {
        this.url = url;
    }

    /**
     * Getter for <code>LINK.DESCRIPTION</code>.
     */
    @Size(max = 1000000000)
    @Nullable
    public String getDescription() {
        return this.description;
    }

    /**
     * Setter for <code>LINK.DESCRIPTION</code>.
     */
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    /**
     * Getter for <code>LINK.LAST_UPDATED</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public OffsetDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    /**
     * Setter for <code>LINK.LAST_UPDATED</code>.
     */
    public void setLastUpdated(@NotNull OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Link other = (Link) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.url == null) {
            if (other.url != null)
                return false;
        }
        else if (!this.url.equals(other.url))
            return false;
        if (this.description == null) {
            if (other.description != null)
                return false;
        }
        else if (!this.description.equals(other.description))
            return false;
        if (this.lastUpdated == null) {
            if (other.lastUpdated != null)
                return false;
        }
        else if (!this.lastUpdated.equals(other.lastUpdated))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.url == null) ? 0 : this.url.hashCode());
        result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
        result = prime * result + ((this.lastUpdated == null) ? 0 : this.lastUpdated.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Link (");

        sb.append(id);
        sb.append(", ").append(url);
        sb.append(", ").append(description);
        sb.append(", ").append(lastUpdated);

        sb.append(")");
        return sb.toString();
    }
}
