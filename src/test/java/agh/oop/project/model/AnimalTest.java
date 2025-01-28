package agh.oop.project.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AnimalTest {
    @Test
    public void testAnimalComparisonEnergy() {
        AbstractMap map = new GlobeMap(Configuration.getDefault());

        Animal animal10 = new Animal(new Genome(10), new Vector2d(0, 0), Rotation.EAST, map, 10);
        Animal animal9 = new Animal(new Genome(10), new Vector2d(0, 0), Rotation.EAST, map, 9);
        Animal animal11 = new Animal(new Genome(10), new Vector2d(0, 0), Rotation.EAST, map, 11);

        Assertions.assertTrue(animal11.compareTo(animal10) < 0);
        Assertions.assertTrue(animal11.compareTo(animal9) < 0);
        Assertions.assertTrue(animal10.compareTo(animal9) < 0);

        Assertions.assertTrue(animal11.compareTo(animal11) == 0);
        Assertions.assertTrue(animal10.compareTo(animal10) == 0);
        Assertions.assertTrue(animal9.compareTo(animal9) == 0);
    }

    @Test
    public void testAnimalComparisonAge() {
        AbstractMap map = new GlobeMap(Configuration.getDefault());

        Animal animalGrandpa = new Animal(new Genome(10), new Vector2d(0, 0), Rotation.EAST, map, map.getConfiguration().initialEnergy() + 2);
        animalGrandpa.move(map);
        animalGrandpa.move(map);
        Animal animalFather = new Animal(new Genome(10), new Vector2d(0, 0), Rotation.EAST, map, map.getConfiguration().initialEnergy() + 1);
        animalFather.move(map);
        Animal animalSon = new Animal(new Genome(10), new Vector2d(0, 0), Rotation.EAST, map, map.getConfiguration().initialEnergy());

        Assertions.assertTrue(animalGrandpa.compareTo(animalFather) < 0);
        Assertions.assertTrue(animalGrandpa.compareTo(animalSon) < 0);
        Assertions.assertTrue(animalFather.compareTo(animalSon) < 0);

        Assertions.assertTrue(animalGrandpa.compareTo(animalGrandpa) < 0);
        Assertions.assertTrue(animalFather.compareTo(animalFather) < 0);
        Assertions.assertTrue(animalSon.compareTo(animalSon) == 0);
    }

    @Test
    public void testAnimalBreeding() {
        AbstractMap map = new GlobeMap(Configuration.getDefault());

        Animal animalFather = new Animal(new Genome(10), new Vector2d(0, 0), Rotation.EAST, map, map.getConfiguration().reproductionEnergyUsage() + 5);
        Animal animalMother = new Animal(new Genome(10), new Vector2d(0, 0), Rotation.EAST, map, map.getConfiguration().reproductionEnergyUsage() + 5);
        Animal animalChild = animalMother.breedWith(animalFather);

        Assertions.assertTrue(animalChild.getEnergy() == map.configuration.reproductionEnergyUsage() * 2);
        Assertions.assertTrue(animalFather.getPosition().equals(animalMother.getPosition()));
        Assertions.assertTrue(animalChild.getPosition().equals(animalFather.getPosition()));
    }
}
