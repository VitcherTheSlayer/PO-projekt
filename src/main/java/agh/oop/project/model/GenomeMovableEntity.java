package agh.oop.project.model;

import java.util.Iterator;

public abstract class GenomeMovableEntity {
    protected final Genome genome;
    protected Iterator<Integer> genomeIterator;
    protected Vector2d position;
    protected Rotation rotation;

    public GenomeMovableEntity(Genome genome, Vector2d position, Rotation rotation) {
        this.genome = genome;
        this.position = position;
        genomeIterator = this.genome.iterator();
    }
    // random
    public GenomeMovableEntity(int genomeLength, Vector2d position, Rotation rotation) {
        genome = new Genome(genomeLength);
        this.position = position;
        genomeIterator = this.genome.iterator();
    }

    public void move(IMoveValidator moveValidator) {
        rotation = rotation.add(genomeIterator.next());

        var nextMove = moveValidator.requestMove(this, position.add(rotation.nextMove()));
        position = nextMove.getKey();
        rotation = nextMove.getValue();
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Vector2d getPosition() {
        return position;
    }
}
