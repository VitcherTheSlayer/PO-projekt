package agh.oop.project.model;

import javafx.util.Pair;

public interface IMoveValidator {
    public Pair<Vector2d, Rotation> requestMove(GenomeMovableEntity genomeMovableEntity, Vector2d requestedPosition);
}
