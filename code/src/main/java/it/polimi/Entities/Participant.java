package it.polimi.Entities;

import java.io.Serializable;
import java.util.List;

public record Participant(int index, String name, String ipAddress) implements Serializable {
    public Participant {
        if (index < 0 || name == null || ipAddress == null) {
            throw new IllegalArgumentException("Name and address must not be null, and index must be >= 0");
        }
    }

}
