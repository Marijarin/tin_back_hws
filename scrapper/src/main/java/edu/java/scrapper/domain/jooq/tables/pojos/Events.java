/*
 * This file is generated by jOOQ.
 */
package edu.java.scrapper.domain.jooq.tables.pojos;


import jakarta.validation.constraints.Size;

import java.beans.ConstructorProperties;
import java.io.Serializable;

import javax.annotation.processing.Generated;

import org.jetbrains.annotations.NotNull;


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
public class Events implements Serializable {

    private static final long serialVersionUID = 1L;

    private String event;
    private Long linkId;

    public Events() {}

    public Events(Events value) {
        this.event = value.event;
        this.linkId = value.linkId;
    }

    @ConstructorProperties({ "event", "linkId" })
    public Events(
        @NotNull String event,
        @NotNull Long linkId
    ) {
        this.event = event;
        this.linkId = linkId;
    }

    /**
     * Getter for <code>EVENTS.EVENT</code>.
     */
    @jakarta.validation.constraints.NotNull
    @Size(max = 1000000000)
    @NotNull
    public String getEvent() {
        return this.event;
    }

    /**
     * Setter for <code>EVENTS.EVENT</code>.
     */
    public void setEvent(@NotNull String event) {
        this.event = event;
    }

    /**
     * Getter for <code>EVENTS.LINK_ID</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public Long getLinkId() {
        return this.linkId;
    }

    /**
     * Setter for <code>EVENTS.LINK_ID</code>.
     */
    public void setLinkId(@NotNull Long linkId) {
        this.linkId = linkId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Events other = (Events) obj;
        if (this.event == null) {
            if (other.event != null)
                return false;
        }
        else if (!this.event.equals(other.event))
            return false;
        if (this.linkId == null) {
            if (other.linkId != null)
                return false;
        }
        else if (!this.linkId.equals(other.linkId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.event == null) ? 0 : this.event.hashCode());
        result = prime * result + ((this.linkId == null) ? 0 : this.linkId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Events (");

        sb.append(event);
        sb.append(", ").append(linkId);

        sb.append(")");
        return sb.toString();
    }
}
