package it.polimi.Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record VectorClock(List<Integer> vector) implements Serializable {

    // True if this vector clock can be delivered after the one passed as argument
    public boolean canBeDeliveredAfter(VectorClock after) {
        List<Integer> other = after.vector();
        if (vector.size() != other.size()) {
            throw new RuntimeException();
        }

        int count = 0;
        for (int i = 0; i < vector.size(); i++) {
            int diff = vector.get(i) - other.get(i);
            if (diff > 0) {
                count++;
                if (diff > 1) {
                    return false;
                }
            }
        }
        return count <= 1;
    }

    // True if this vector clock is surely older than the one passed as argument
    public boolean isOlder(VectorClock other) {
        List<Integer> otherVector = other.vector();
        for (int i = 0; i < otherVector.size(); i++) {
            if (vector.get(i) > otherVector.get(i)) return false;
        }
        return true;
    }

    public boolean isYoungerExceptForOne(VectorClock other) {
        List<Integer> otherVector = other.vector();
        int count = 0;
        for (int i = 0; i < otherVector.size(); i++) {
            if (vector.get(i) > otherVector.get(i)) count++;
            if (count > 1) return false;
        }
        return true;
    }

    // Increments the value at the given index by one
    public VectorClock increment(int index) {
        List<Integer> newVector = new ArrayList<>(vector);
        newVector.set(index, newVector.get(index) + 1);
        return new VectorClock(newVector);
    }

    // Merges two vector clocks
    public VectorClock merge(VectorClock other) {
        List<Integer> otherVector = other.vector();
        if (vector.size() != otherVector.size()) {
            throw new RuntimeException();
        }

        List<Integer> newVector = new ArrayList<>();
        for (int i = 0; i < vector.size(); i++) {
            int el = Math.max(vector.get(i), otherVector.get(i));
            newVector.add(el);
        }
        return new VectorClock(newVector);
    }

    // Converts a string representing a list into the corresponding Vector Clock element
    public static VectorClock parseVectorClock(String vector) {
        return new VectorClock(
                Arrays.stream(vector.substring(1, vector.length() - 1) // Remove brackets
                                .replace(" ", "")
                                .split(",")
                        )
                        .map(Integer::valueOf)
                        .toList()
        );
    }

    @Override
    public String toString() {
        return vector.toString();
    }
}
