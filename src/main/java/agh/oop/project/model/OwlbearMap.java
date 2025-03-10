package agh.oop.project.model;

import javafx.util.Pair;
import agh.oop.project.model.util.RandomPositionGenerator;

import java.util.Random;
import java.util.SortedSet;

public class OwlbearMap extends GlobeMap {
    protected final Boundary huntingGround;
    private final Owlbear owlbear;

    protected OwlbearMap(Configuration configuration) {
        super(configuration);
        int width = configuration.width();
        int height = configuration.height();

        // Długość boku terenu łowieckiego
        int c = (int) Math.sqrt(0.2 * width * height);

        Vector2d lowerLeft = RandomPositionGenerator.getRandomPosition(width-c-1, height-c-1);
        Vector2d upperRight = lowerLeft.add(new Vector2d(c,c));
        huntingGround = new Boundary(lowerLeft, upperRight);

        // Tworzenie Owlbear
        Random rng = new Random();
        Vector2d randomPlace = RandomPositionGenerator.getRandomPositionWithinBounds(huntingGround);
        Rotation rot = Rotation.get(rng.nextInt(Genome.UNIQUE_GENES_COUNT));
        this.owlbear = new Owlbear(configuration.genomeLength(),randomPlace,rot);
    }

    @Override
    public Pair<Vector2d, Rotation> requestMove(GenomeMovableEntity genomeMovableEntity, Vector2d requestedPosition) {
        if (genomeMovableEntity instanceof Owlbear) {
            // Logika specyficzna dla Owlbear
            if (!huntingGround.test(requestedPosition)) {
                Rotation newRotation = genomeMovableEntity.getRotation().add(Genome.UNIQUE_GENES_COUNT);
                return new Pair<>(genomeMovableEntity.getPosition(), newRotation); // Zatrzymaj w miejscu i zmień rotację
            } else {
                int newX = requestedPosition.getX();
                int newY = requestedPosition.getY();

                if (newX < huntingGround.lowerLeft().getX() ||
                        newX > huntingGround.upperRight().getX()) {
                    newX = genomeMovableEntity.getPosition().getX();
                    Rotation newRotation = genomeMovableEntity.getRotation().add(Genome.UNIQUE_GENES_COUNT);
                    return new Pair<>(new Vector2d(newX, genomeMovableEntity.getPosition().getY()), newRotation);
                }

                if (newY < huntingGround.lowerLeft().getY() ||
                        newY > huntingGround.upperRight().getY()) {
                    newY = genomeMovableEntity.getPosition().getY();
                    Rotation newRotation = genomeMovableEntity.getRotation().add(Genome.UNIQUE_GENES_COUNT);
                    return new Pair<>(new Vector2d(genomeMovableEntity.getPosition().getX(), newY), newRotation);
                }

                return new Pair<>(new Vector2d(newX, newY), genomeMovableEntity.getRotation());
            }
        } else {
            return super.requestMove(genomeMovableEntity, requestedPosition);
        }
    }

    public void huntAnimals(int day) {
        SortedSet<Animal> animalsOnPosition = assureSetFor(owlbear.getPosition());

        if (!animalsOnPosition.isEmpty()) {
            for (Animal animal : animalsOnPosition) {
                animal.die(day, "Eaten");
            }
            animalsOnPosition.clear();
        }
    }

    public Boundary getHuntingGround() {
        return huntingGround;
    }

    protected void afterPreyUpdate() {
        Vector2d owlbearPosition = owlbear.getPosition();

        animalOccupiedPositions.remove(owlbearPosition);
        animalsMap.remove(owlbearPosition);
    }

    public Owlbear getOwlbear() {
        return owlbear;
    }
}
