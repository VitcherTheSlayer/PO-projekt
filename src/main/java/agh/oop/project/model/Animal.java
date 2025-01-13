package agh.oop.project.model;

public class Animal extends GenomeMovableEntity{
    private int energy;

    public Animal(Genome genome, Vector2d position) {
        super(genome, position);
    }

    public Animal(int genomeLength, Vector2d position) {
        super(genomeLength, position);
    }

    public int getEnergy() {
        return energy;
    }
}
