package agh.oop.project.model;

import agh.oop.project.model.util.RandomPositionGenerator;

import java.util.Random;

public class SimulationOwlbear extends Simulation {
    private Owlbear owlbear;
    private OwlbearMap map;
    public SimulationOwlbear(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected AbstractMap createMap(Configuration configuration) {
        this.map = new OwlbearMap(configuration);
        return map;
    }

    @Override
    protected void specificDailyLogic() {
        System.out.println("Ruszam owl");
        owlbear.move(map);
        map.prey(day); // Owlbear poluje
    }

    @Override
    public void createMapElements(){
        super.createMapElements();
        Random rng = new Random();
        Boundary bounds = map.getHuntingGround();
        Vector2d randomPlace = RandomPositionGenerator.getRandomPositionWithinBounds(bounds);
        Rotation rot = Rotation.get(rng.nextInt(Genome.UNIQUE_GENES_COUNT));
        this.owlbear = new Owlbear(configuration.genomeLength(),randomPlace,rot);
        map.setOwlbear(owlbear);
    }
}

