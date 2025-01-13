package agh.oop.project.model;

import java.util.Comparator;

public class AnimalEnergyComparator implements Comparator<Animal> {
    @Override
    public int compare(Animal animal1, Animal animal2) {
        return animal1.getEnergy() - animal2.getEnergy();
    }
}
