package agh.oop.project.model;

import java.util.*;

public class Animal extends GenomeMovableEntity implements Comparable<Animal> {
    private int energy;
    private final AbstractMap map;
    private int age = 0;
    private final List<Animal> children = new ArrayList<>();
    private int grassEaten = 0;
    private int deathTime = -1;
    private String deathReason = "";
    private int energyUsage = 1;

    public Animal(Genome genome, Vector2d position, Rotation rotation, AbstractMap map) {
        super(genome, position, rotation);
        this.map = map;
        this.energy = map.configuration.initialEnergy();
    }

    public Animal(Genome genome, Vector2d position, Rotation rotation, AbstractMap map, int energy) {
        this(genome, position, rotation, map);
        this.energy = energy;
    }

    public Animal(int genomeLength, Vector2d position, Rotation rotation, AbstractMap map) {
        super(genomeLength, position, rotation);
        this.map = map;
        this.energy = map.configuration.initialEnergy();
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
        Rotation newRotation = Rotation.get(new Random().nextInt(Genome.UNIQUE_GENES_COUNT));

        return new Animal(
                newGenome,
                position,
                newRotation,
                map,
                2 * map.getConfiguration().reproductionEnergyUsage()
        );
    }

    @Override
    public int compareTo(Animal o) {
        // ordered by:
        // 0. the same object
        // 1. more energetic
        // 2. older
        // 3. child count
        // 4. random

        if(o == this){
            return 0;
        }

        int energyDiff = o.energy - energy;
        if(energyDiff != 0) {
            return energyDiff;
        }

        int ageDiff = o.age - age;
        if(ageDiff != 0) {
            return ageDiff;
        }

        int childrenDiff = o.children.size() - children.size();
        if(childrenDiff != 0) {
            return childrenDiff;
        }

        return new Random().nextBoolean() ? 1 : -1;
    }

    @Override
    public Vector2d move(IMoveValidator moveValidator) {
        ++age;
        energy -= energyUsage;
        return super.move(moveValidator);
    }

    public void eatGrass(){
        energy += map.getConfiguration().energyPerPlant();
        ++grassEaten;
    }

    public int getGrassEaten() {
        return grassEaten;
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

    public int getAge() {
        return age;
    }
    public void addOffspring(Animal animal) {
        this.children.add(animal);
    }

    public boolean isAlive() {
        return deathTime == -1;
    }

    public int getDeathTime() {
        return deathTime;
    }
}
