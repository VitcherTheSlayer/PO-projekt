package agh.oop.project.model;

import javafx.util.Pair;

import java.util.List;

public class GlobeMap extends AbstractMap {
    protected GlobeMap(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Pair<Vector2d, Rotation> requestMove(GenomeMovableEntity genomeMovableEntity, Vector2d requestedPosition) {
        if (genomeMovableEntity instanceof Animal) {
            int newX = requestedPosition.getX();
            if (newX < boundary.lowerLeft().getX()) {
                newX = boundary.upperRight().getX();
            } else if (newX > boundary.upperRight().getX()) {
                newX = boundary.lowerLeft().getX();
            }

            Rotation newRotation = genomeMovableEntity.getRotation();
            int newY = requestedPosition.getY();
            if (newY < boundary.lowerLeft().getY() || newY > boundary.upperRight().getY()) {
                newRotation = newRotation.add(Genome.UNIQUE_GENES_COUNT / 4);
                newY = genomeMovableEntity.getPosition().getY(); // Czasami wychodziło poza mape zwierze
            }

            return new Pair<>(new Vector2d(newX, newY), newRotation);
        }
        return null; // Powinno nigdy tego nie zwrócić
    }
}
