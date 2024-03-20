package edu.java.client.model;

import java.util.HashMap;
import java.util.Map;

public enum Event {
    Created {
        @Override
        public String getMeaning() {
            return "CreateEvent";
        }

        @Override
        public String getDescription() {
            return Created.heading + "A Git branch or tag is created!";
        }
    },
    IssueComment {
        @Override
        public String getMeaning() {
            return "IssueCommentEvent";
        }

        @Override
        public String getDescription() {
            return IssueComment.heading + "Somebody made a comment!";
        }
    },
    Answer {
        @Override
        public String getMeaning() {
            return "answer";
        }

        @Override
        public String getDescription() {
            return Answer.heading + "Somebody suggested a solution!";
        }
    },
    SOFComment {
        @Override
        public String getMeaning() {
            return "comment";
        }

        @Override
        public String getDescription() {
            return SOFComment.heading + "Somebody commented this question!";
        }
    };
    private final String heading = "Check out a latest update ->> ";
    public abstract String getMeaning();
    public abstract String getDescription();

    public Map<String, Event> getEventMap(){
        var map = new HashMap<String, Event>();
        switch (this) {
            case Answer -> map.put(Answer.getMeaning(), Answer);
            case SOFComment -> map.put(SOFComment.getMeaning(), SOFComment);
            case Created -> map.put(Created.getMeaning(), Created);
            case IssueComment -> map.put(IssueComment.getMeaning(), IssueComment);
        }
        return map;
    }
}
