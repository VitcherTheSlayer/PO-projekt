package agh.oop.project.model;

import javafx.util.Pair;

import java.util.List;

public class GlobeMap extends AbstractMap {
    protected GlobeMap(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Pair<Vector2d, Rotation> requestMove(GenomeMovableEntity genomeMovableEntity, Vector2d requestedPosition) {
        return handleRegularMove(genomeMovableEntity, requestedPosition);
    }

    @Override
    public void mapChanged(AbstractMap worldMap, String message) {
    }
}
