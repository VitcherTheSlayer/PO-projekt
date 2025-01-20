package agh.oop.project.model;

import java.util.*;

public abstract class AbstractMap implements IMoveValidator {
    protected final Map<Vector2d, SortedSet<Animal>> animalsMap = new HashMap<>();
    protected final Map<Vector2d, Grass> grassMap = new HashMap<>(); // Jak objekt grass ma w sobie wektor to tutaj niepotrzebnie kopiuje się
    protected final Configuration configuration;
    protected final IMutator mutator;
    protected final Boundary boundary;

    protected AbstractMap(Configuration configuration) {
        this.configuration = configuration;
        this.mutator = switch (configuration.mutationVariant()) {
            case SWAP -> new SwapMutator(configuration.minMutations(), configuration.maxMutations(), configuration.swapMutationPercent());
            case FULL_RANDOMNESS -> new FullyRandomMutator(configuration.minMutations(), configuration.maxMutations());
        };
        this.boundary = new Boundary(new Vector2d(0, 0), new Vector2d(configuration.width(), configuration.height()));
    }

    protected void makeSetFor(Vector2d position) {
        animalsMap.put(position, new TreeSet<>(new AnimalEnergyComparator()));
    }

    Configuration getConfiguration(){
        return configuration;
    }

    public IMutator getMutator() {
        return mutator;
    }

    public Boundary getBoundary() {
        return new Boundary(boundary.lowerLeft(), boundary.upperRight());
    }
}
