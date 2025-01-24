package agh.oop.project.model;

import agh.oop.project.presenter.SimulationWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static agh.oop.project.model.MapVariant.GLOBE;

public abstract class Simulation {
    protected final Configuration configuration;
    private final List<Animal> animals = new ArrayList<>();
    private static final Vector2d VECTORZERO =  new Vector2d(0,0);
    protected static AbstractMap map;
    private SimulationWindow window;
    protected int day = 1;

    public Simulation(Configuration configuration) {
        this.configuration = configuration;
        map = createMap(configuration);
    }

    protected abstract void specificDailyLogic();

    protected abstract AbstractMap createMap(Configuration configuration);

    public void setSimulationWindow(SimulationWindow window){
        this.window = window;
    }

    public AbstractMap getMap() {
        return map;
    }

    public void resume(){}
    public void pause(){}


    public void createMapElements(){

        Random rng = new Random();

        map.growInitialGrass();

        // Tworze randomowo  zwierzęta
        for (int animalNr = 0; animalNr < configuration.initialAnimals(); animalNr++){
            Vector2d randomPlace = new Vector2d(rng.nextInt(configuration.width()), rng.nextInt(configuration.height()));
            Rotation rot = Rotation.get(rng.nextInt(Genome.UNIQUE_GENES_COUNT));
            Animal animal = new Animal(configuration.genomeLength(), randomPlace, rot, map);
            animals.add(animal);
            map.addAnimal(animal);
        }
    }

    public void run(){
        while (day < 50) {
            // Tutaj jakaś logia zmiany dnia czy coś
            dailyCycle();
            window.mapChanged();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            day++;
        }
    }

    private void dailyCycle() {
        specificDailyLogic();
        map.growGrass(configuration.plantGrowthPerDay());
        map.moveAnimals();
        map.feast();
        map.breed();
    }
}
