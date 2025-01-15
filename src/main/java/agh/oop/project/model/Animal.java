package agh.oop.project.model;

import java.util.*;

public class Animal extends GenomeMovableEntity implements Comparable<Animal> {
    private int energy;
    private AbstractMap map;
    private int age = 0;
    private final List<Animal> children = new ArrayList<>();
    private int grassEaten = 0;
    private int deathTime = -1;
    private String deathReason = "";

    public Animal(Genome genome, Vector2d position, Rotation rotation, AbstractMap map) {
        super(genome, position, rotation);
        this.map = map;
    }

    public Animal(int genomeLength, Vector2d position, Rotation rotation, AbstractMap map) {
        super(genomeLength, position, rotation);
        this.map = map;
    }

    public int getEnergy() {
        return energy;
    }

    // called once per tile by the dominant Animal
    public Animal breedWith(Animal other) {
        if(!breedable() || !other.breedable()) {
            return null;
        }

        energy -= map.getConfiguration().reproductionEnergyUsage();
        other.energy -= map.getConfiguration().reproductionEnergyUsage();

        Genome newGenome = map
                .getMutator()
                .mutate(GenomeCombiner
                        .combine(
                                genome,
                                other.genome,
                                energy,
                                other.energy
                        )
                );
        Rotation newRotation = Rotation.NORTH.add(new Random().nextInt(Genome.UNIQUE_GENES_COUNT));

        return new Animal(newGenome, position, newRotation, map);
    }

    @Override
    public int compareTo(Animal o) {
        return energy - o.energy;
    }

    @Override
    public void move(IMoveValidator moveValidator) {
        super.move(moveValidator);
        ++age;
    }

    public void eatGrass(){
        energy += map.getConfiguration().energyPerPlant();
        ++grassEaten;
    }

    public float energyFraction() {
        return Math.min(1.0f, (float)energy / (float)map.getConfiguration().initialEnergy());
    }

    public boolean breedable() {
        return energy >= map.getConfiguration().reproductionMinEnergy();
    }

    public void die(int time, String reason) {
        energy = 0;
        this.deathTime = time;
        this.deathReason = reason;
    }

    public int childrenCount() {
        return children.size();
    }

    public int descendantsCount() {
        // dumb idea, allows for duplicates
//        int count = 0;
//        for(Animal child : children) {
//            count += child.descendantsCount() + 1;
//        }
//        return count;

        int count = 0;
        Animal curr = null;

        List<Animal> toProcess = new ArrayList<>(this.children);
        Set<Animal> processed = new HashSet<>();

        while (!toProcess.isEmpty()) {
            curr = toProcess.removeLast();
            if(!processed.contains(curr)) {
                ++count;
                toProcess.addAll(curr.children);
                processed.add(curr);
            }
        }

        return count - 1;
    }
}
