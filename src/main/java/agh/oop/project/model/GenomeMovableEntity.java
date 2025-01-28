package agh.oop.project.model;

import java.util.Iterator;

public abstract class GenomeMovableEntity {
    protected final Genome genome;
    protected Genome.GenomeIterator genomeIterator;
    protected Vector2d position;
    protected Rotation rotation;

    public GenomeMovableEntity(Genome genome, Vector2d position, Rotation rotation) {
        this.genome = genome;
        this.position = position;
        this.rotation = rotation;
        genomeIterator = this.genome.iterator();
    }
    // random
    public GenomeMovableEntity(int genomeLength, Vector2d position, Rotation rotation) {
        genome = new Genome(genomeLength);
        this.position = position;
        this.rotation = rotation;
        genomeIterator = this.genome.iterator();
    }

    public Vector2d move(IMoveValidator moveValidator) {
        rotation = rotation.add(genomeIterator.next());
        var nextMove = moveValidator.requestMove(this, position.add(rotation.nextMove()));
        position = nextMove.getKey();
        rotation = nextMove.getValue();

        return position;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Vector2d getPosition() {
        return position;
    }

    public Genome getGenome() {
        return genome;
    }

    public int getActiveGeneIdx() {
        return genomeIterator.idx();
    }
}
