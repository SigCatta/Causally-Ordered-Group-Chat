package it.polimi.Entities;

import java.io.Serializable;

public record Message(String text, VectorClock vectorClock) implements Serializable {
    public Message {
        if (text == null || vectorClock == null) {
            throw new IllegalArgumentException("Message text and vector clock must not be null");
        }
        text = text.replace("\n", " ");
    }

    public boolean sameMessage(Message m1, Message m2) {
        return m1.text().equals(m2.text()) && m1.vectorClock().equals(m2.vectorClock());
    }
}
