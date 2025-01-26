package agh.oop.project.model;

public class SimulationFactory {
    public static Simulation createSimulation(Configuration configuration) {
        switch (configuration.mapVariant()) {
            case GLOBE:
                return new SimulationGlobe(configuration);
            case WILD_OWLBEAR:
                return new SimulationOwlbear(configuration);
            default:
                throw new IllegalArgumentException("Unknown map variant");
        }
    }
}

