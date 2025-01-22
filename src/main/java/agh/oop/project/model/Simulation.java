package agh.oop.project.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Simulation {
    private final Configuration configuration;
    private final List<Animal> animals = new ArrayList<>();
    private static final Vector2d VECTORZERO =  new Vector2d(0,0);
    private static AbstractMap map;

    public Simulation(Configuration configuration) {
        this.configuration = configuration;
        AbstractMap map = new GlobeMap(configuration);
        Simulation.map = map;
//        createMapElements();
    }

    public AbstractMap getMap() {
        return map;
    }

    public void resume(){}
    public void pause(){}


    public void createMapElements(){

        // Tworze randomowo rośliny
        Random rng = new Random();
        List<Vector2d> preferedAreas = map.getPreferedAreas();
        List<Vector2d> norPreferedAreas = map.getNotPreferedAreas();
        Collections.shuffle(preferedAreas);
        Collections.shuffle(norPreferedAreas);

        for (int plantNr = 0; plantNr < configuration.initialPlants(); plantNr++){
            if (preferedAreas.isEmpty() && norPreferedAreas.isEmpty()) {
                throw new IllegalStateException("Nie ma wystarczającej liczby unikalnych miejsc dla wszystkich roślin.");
            }

            int chance = rng.nextInt(100);
            Vector2d randomPlace;
            if (chance < 80 && !preferedAreas.isEmpty()) {
                randomPlace = preferedAreas.removeFirst();
            } else {
                randomPlace = norPreferedAreas.removeFirst();
            }

            Grass grass = new Grass(randomPlace);
            map.placeGrass(grass);
        }

        List<Vector2d> allAreas = new ArrayList<>();
        allAreas.addAll(preferedAreas);
        allAreas.addAll(norPreferedAreas);
        Collections.shuffle(allAreas);

        // Tworze randomowo  zwierzęta
        for (int animalNr = 0; animalNr <configuration.initialAnimals(); animalNr++){
            if (allAreas.isEmpty()) {
                throw new IllegalStateException("Nie ma wystarczającej liczby unikalnych miejsc dla wszystkich zwierząt.");
            }
            Vector2d randomPlace = allAreas.removeFirst();
            int genomeLenght = configuration.genomeLength();
            int dir = rng.nextInt(0,7);
            Rotation rot = Rotation.get(dir);
            Animal animal = new Animal(genomeLenght,randomPlace,rot,map);
            animals.add(animal);
            map.placeAnimal(animal);
        }
    }

    public void run(){
        int day = 1;
        while (day < 5) {
            // Tutaj jakaś logia zmiany dnia czy coś
            for (Animal animal : animals) {
                System.out.println("ruch "+day);
                System.out.println("anima prev pos" + animal.getPosition());
                Vector2d oldPosition = animal.getPosition();
                animal.move(map);
                System.out.println("animal new pos" + animal.getPosition());
                map.move(animal,oldPosition);
            }

            try {
                Thread.sleep(1000);  // Zatrzymuje wykonanie na 1000 milisekund (czyli 1 sekundę)
            } catch (InterruptedException e) {
                e.printStackTrace();  // Obsługuje przerwanie wątku, jeśli wystąpi
            }


            day++;
        }
    }
}
