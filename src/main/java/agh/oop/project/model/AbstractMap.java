package agh.oop.project.model;

import java.util.*;

public abstract class AbstractMap implements MoveValidator {
    protected final Map<Vector2d, SortedSet<Animal>> animalsMap = new HashMap<>();
    protected final Map<Vector2d, Grass> grassMap = new HashMap<>();

    protected void makeSetFor(Vector2d position) {
        animalsMap.put(position, new TreeSet<>(new AnimalEnergyComparator()));
    }
}
