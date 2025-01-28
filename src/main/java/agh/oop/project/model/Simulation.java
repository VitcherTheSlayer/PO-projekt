package agh.oop.project.model;

import agh.oop.project.presenter.SimulationWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import static agh.oop.project.model.MapVariant.GLOBE;

public abstract class Simulation {
    protected final Configuration configuration;
    private final List<Animal> animals = new ArrayList<>();
    private static final Vector2d VECTORZERO =  new Vector2d(0,0);
    protected static AbstractMap map;
    private SimulationWindow window;
    protected int day = 1;
    private volatile boolean isPaused = false;
    private volatile boolean stopSimulation = false;
    StatsCollector statsCollector;

    public Simulation(Configuration configuration) {
        this.configuration = configuration;
        map = createMap(configuration);
        this.statsCollector = new StatsCollector(map);
    }

    protected abstract void specificDailyLogic();

    protected abstract AbstractMap createMap(Configuration configuration);

    public void setSimulationWindow(SimulationWindow window){
        this.window = window;
    }

    public AbstractMap getMap() {
        return map;
    }

    public void resume(){
        synchronized (this) {
            isPaused = false;
            this.notifyAll();
        }
    }
    public void pause(){
        isPaused = true;
    }


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

    public void run() throws InterruptedException {
        // first draw
        Semaphore semaphore = new Semaphore(0);
        window.mapChanged(semaphore);
        semaphore.acquire();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (day < 50 && !map.getAnimals().isEmpty()) {

            synchronized (this){
                while (isPaused) {
                    this.wait();
                }
            }

            dailyCycle(day);

            // semaphore to wait for map being draw
            window.mapChanged(semaphore);
            semaphore.acquire();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            day++;
        }
    }

    private void dailyCycle(int day) {
        specificDailyLogic();
        statsCollector.addData(map,day);
        map.beforeEatUpdate(day);
        map.growGrass(configuration.plantGrowthPerDay());
        map.moveAnimals();
        map.feast();
        map.breed();
    }

    public int getDay() {
        return day;
    }

    public StatsCollector getStatsCollector() {
        return statsCollector;
    }
}
