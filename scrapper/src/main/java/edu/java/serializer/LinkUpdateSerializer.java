package edu.java.serializer;

import org.apache.kafka.common.serialization.Serializer;
import scrapper.LinkUpdateOuterClass;

public class LinkUpdateSerializer implements Serializer<LinkUpdateOuterClass.LinkUpdate> {
    @Override
    public byte[] serialize(String s, LinkUpdateOuterClass.LinkUpdate linkUpdate) {
        return linkUpdate.toByteArray();
    }
}
