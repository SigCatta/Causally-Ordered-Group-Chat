package it.polimi.Entities;

public record Participant(String name, String ipAddress) {
    public Participant {
        if (name == null || ipAddress == null) {
            throw new IllegalArgumentException("Name and address must not be null");
        }
    }
}
