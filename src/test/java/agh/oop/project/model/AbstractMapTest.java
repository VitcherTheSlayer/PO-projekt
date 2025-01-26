package agh.oop.project.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbstractMapTest {
    @Test
    public void animalDontCrossBorder() {
        // Given
        GlobeMap map = new GlobeMap(Configuration.getDefault());
        Animal animal = new Animal(new Genome(List.of(1)),new Vector2d(2,0),Rotation.SOUTH,map);

        // When
        animal.move(map);

        // Then
//        assertEquals(Rotation.NORTH,animal.getRotation());
        assertEquals(new Vector2d(2,1),animal.getPosition());
    }


}