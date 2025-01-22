package agh.oop.project.model;

import java.util.function.Predicate;

public record Boundary(Vector2d lowerLeft, Vector2d upperRight) implements Predicate<Vector2d> {
    @Override
    public boolean test(Vector2d vec) {
        return lowerLeft.precedes(vec) && upperRight.follows(vec);
    }
}