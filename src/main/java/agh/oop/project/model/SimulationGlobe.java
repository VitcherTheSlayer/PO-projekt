package agh.oop.project.model;

public class SimulationGlobe extends Simulation {
    public SimulationGlobe(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected AbstractMap createMap(Configuration configuration) {
        return new GlobeMap(configuration);
    }

    @Override
    protected void specificDailyLogic() {
        // Brak dodatkowej logiki
    }
}

