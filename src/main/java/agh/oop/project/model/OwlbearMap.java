package agh.oop.project.model;

import javafx.util.Pair;
import agh.oop.project.model.util.RandomPositionGenerator;

import java.util.SortedSet;

public class OwlbearMap extends AbstractMap {
    protected final Boundary huntingGround;
    private Owlbear owlbear;

    protected OwlbearMap(Configuration configuration) {
        super(configuration);
        int width = configuration.width();
        int height = configuration.height();

        Vector2d lowerLeft = RandomPositionGenerator.getRandomPosition(width, height);
        if (2*lowerLeft.getY() - height > 0) { // Gdy teren dropnie za wysoko
            lowerLeft.add(new Vector2d(-(2*lowerLeft.getY() - height),0 ));
        }
        Vector2d upperRight = lowerLeft.add(new Vector2d((int) (width * 0.45), (int) (height * 0.45)));
        huntingGround = new Boundary(lowerLeft, upperRight);
    }

    @Override
    public Pair<Vector2d, Rotation> requestMove(GenomeMovableEntity genomeMovableEntity, Vector2d requestedPosition) {
        Vector2d newPosition = requestedPosition;
        Rotation newRotation = genomeMovableEntity.getRotation();

        // Sprawdzenie, czy mamy do czynienia z Owlbear-em
        if (genomeMovableEntity instanceof Owlbear) {
            if (!huntingGround.test(requestedPosition)) {
                newRotation = newRotation.add(Genome.UNIQUE_GENES_COUNT);
            }
        } else {
            int newX = requestedPosition.getX();
            if (newX < boundary.lowerLeft().getX()) {
                newX = boundary.upperRight().getX();
            } else if (newX > boundary.upperRight().getX()) {
                newX = boundary.lowerLeft().getX();
            }

            int newY = requestedPosition.getY();
            if (newY < boundary.lowerLeft().getY() || newY > boundary.upperRight().getY()) {
                newRotation = newRotation.add(Genome.UNIQUE_GENES_COUNT);
            }

            newPosition = new Vector2d(newX, newY);
        }

        return new Pair<>(newPosition, newRotation);
    }


    @Override
    public void mapChanged(AbstractMap worldMap, String message) {}

    public void prey(int day) {
        if (animalOccupiedPositions.contains(owlbear.getPosition())) {
            SortedSet<Animal> animalsOnPosition = animalsMap.get(owlbear.getPosition());
            if (animalsOnPosition != null) {
                for (Animal animal : animalsOnPosition) {
                    animal.die(day,"Eaten");
                }
            }
        }
    }

    public Boundary getHuntingGround() {
        return huntingGround;
    }

    @Override
    public int objectAt(Vector2d position) {
        if (owlbear.getPosition().equals(position)) {
            return 4;
        }
        int result = super.objectAt(position);
        return result;
    }

    public void setOwlbear(Owlbear owlbear) {
        this.owlbear = owlbear;
    }
}
