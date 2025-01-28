package agh.oop.project.model;

import java.io.IOException;

public class SimulationGlobe extends Simulation {
    public SimulationGlobe(Configuration configuration) throws IOException {
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

