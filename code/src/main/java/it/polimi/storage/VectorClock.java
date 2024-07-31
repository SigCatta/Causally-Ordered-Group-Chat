package it.polimi.storage;

import java.util.List;

public record VectorClock(List<Integer> vector) {

    // True if this vector clock can be delivered after the one passed as argument
    public boolean canBeDelivered(VectorClock after) {
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

    // Increments the value at the given index by one
    public VectorClock increment(int index) {
        List<Integer> newVector = List.copyOf(vector);
        newVector.set(index, newVector.get(index) + 1);
        return new VectorClock(newVector);
    }

    // Merges two vector clocks
    public VectorClock merge(VectorClock other) {
        List<Integer> otherVector = other.vector();
        if (vector.size() != otherVector.size()) {
            throw new RuntimeException();
        }

        List<Integer> newVector = List.copyOf(vector);
        for (int i = 0; i < vector.size(); i++) {
            newVector.set(i, Math.max(vector.get(i), otherVector.get(i)));
        }
        return new VectorClock(newVector);
    }

    @Override
    public String toString() {
        return vector.toString();
    }
}
