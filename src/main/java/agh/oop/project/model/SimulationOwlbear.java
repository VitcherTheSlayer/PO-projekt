package agh.oop.project.model;

import agh.oop.project.model.util.RandomPositionGenerator;

import java.io.IOException;
import java.util.Random;

public class SimulationOwlbear extends Simulation {
    private Owlbear owlbear;
    private OwlbearMap map;
    public SimulationOwlbear(Configuration configuration) throws IOException {
        super(configuration);
    }

    @Override
    protected AbstractMap createMap(Configuration configuration) {
        this.map = new OwlbearMap(configuration);
        return map;
    }

    @Override
    protected void specificDailyLogic() {
        map.getOwlbear().move(map);
        map.huntAnimals(day); // Owlbear poluje
        map.afterPreyUpdate();
    }

    @Override
    public void createMapElements(){
        super.createMapElements();
    }
}

