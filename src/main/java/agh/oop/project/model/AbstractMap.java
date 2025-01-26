package agh.oop.project.model;

import agh.oop.project.model.util.RandomPositionGenerator;
import javafx.util.Pair;

import java.util.*;

public abstract class AbstractMap implements IMoveValidator,MapChangeListener {
    protected final Map<Vector2d, SortedSet<Animal>> animalsMap = new HashMap<>();
    protected final Set<Vector2d> animalOccupiedPositions = new HashSet<>();
    protected final Map<Vector2d, Grass> grassMap = new HashMap<>();
    protected final Configuration configuration;
    protected final IMutator mutator;
    protected final Boundary boundary;
    protected final Vector2d equatorDelta;
    protected final Boundary equator;
    protected final int equatorThickness;
    protected static final int EQUATOR_AREA_PERCENT = 20;
    protected static final int EQUATOR_GROWTH_PERCENT = 80;
    private final UUID uuid = UUID.randomUUID();

    protected AbstractMap(Configuration configuration) {
        this.configuration = configuration;
        this.mutator = switch (configuration.mutationVariant()) {
            case SWAP -> new SwapMutator(
                    configuration.minMutations(),
                    configuration.maxMutations(),
                    configuration.swapMutationPercent()
            );
            case FULL_RANDOMNESS -> new FullyRandomMutator(configuration.minMutations(), configuration.maxMutations());
        };
        this.boundary = new Boundary(
                new Vector2d(0, 0),
                new Vector2d(configuration.width(), configuration.height())
        );

        equatorThickness = configuration.height() * EQUATOR_AREA_PERCENT / 100;
        equatorDelta = new Vector2d(0, (configuration.height() - equatorThickness) / 2);
        equator = new Boundary(equatorDelta, equatorDelta.add(new Vector2d(configuration.width(), equatorThickness - 1)));
    }

    public void addAnimal(Animal animal) {
        assureSetFor(animal.getPosition()).add(animal);
        animalOccupiedPositions.add(animal.getPosition());
    }

    public void removeAnimal(Animal animal) {
        SortedSet<Animal> animals = assureSetFor(animal.getPosition());
        animals.remove(animal);
        if(animals.isEmpty()) {
            animalOccupiedPositions.remove(animal.getPosition());
        }
    }

    public List<Animal> getAnimals() {
        return animalsMap
                .values()
                .stream()
                //.map(set -> set.stream().toList())
                .flatMap(SortedSet::stream)
                .toList();
    }

    public void moveAnimal(Animal animal) {
        removeAnimal(animal);
        Vector2d newPos = animal.move(this);
        addAnimal(animal);
    }

    public void moveAnimals() {
        for(Animal animal : getAnimals()) {
            moveAnimal(animal);
        }
    }

    public Animal getFirstAnimalAt(Vector2d position) {
        SortedSet<Animal> animals = assureSetFor(position);
        if(animals.isEmpty()) {
            return null;
        }
        return animals.getFirst();
    }

    public void feast() {
        for(Vector2d occupiedPos : animalOccupiedPositions) {
            if(grassMap.containsKey(occupiedPos)) {
                grassMap.remove(occupiedPos);
                assureSetFor(occupiedPos).getFirst().eatGrass();
            }
        }
    }

    public void breed() {
        for(Vector2d pos : animalOccupiedPositions) {
            SortedSet<Animal> currentSet = assureSetFor(pos);
            if(currentSet.size() >= 2){
                var it = currentSet.iterator();
                Animal first = it.next();
                Animal second = it.next();
                if (second.breedable()) {
                    Animal offspring = first.breedWith(second);
                    if (offspring != null) { // Czsami null returnowało i był null pointer exp.
                        addAnimal(offspring);
                    }
                }
            }
        }
    }

    protected void growGrass(int count) {
        // "... Istnieje 80% szansy, że nowa roślina wyrośnie na preferowanym polu,
        //  a tylko 20% szans, że wyrośnie na polu drugiej kategorii ..."

        int equatorGrassCount = count * EQUATOR_GROWTH_PERCENT / 100;
        int otherGrassCount = count - equatorGrassCount;

        // Zbieram wolne miejsca na równiku
        List<Vector2d> freeEquatorPositions = new ArrayList<>();
        for (int x = 0; x <= configuration.width(); x++) {
            for (int y = equatorDelta.getY(); y < equatorDelta.getY() + equatorThickness; y++) {
                Vector2d position = new Vector2d(x, y);
                if (!grassMap.containsKey(position) && !animalOccupiedPositions.contains(position)) {
                    freeEquatorPositions.add(position);
                }
            }
        }

        // Zbieram wolne miejsca poza równikiem
        List<Vector2d> freeOtherPositions = new ArrayList<>();
        for (int x = 0; x <= configuration.width(); x++) {
            for (int y = 0; y <= configuration.height(); y++) {
                Vector2d position = new Vector2d(x, y);
                if (!equator.test(position) && !grassMap.containsKey(position) && !animalOccupiedPositions.contains(position)) {
                    freeOtherPositions.add(position);
                }
            }
        }

        // Sprawdzam dostępność miejsc i modyfikuje licznik
        if (freeEquatorPositions.size() < equatorGrassCount) {
            otherGrassCount += equatorGrassCount - freeEquatorPositions.size();
            equatorGrassCount = freeEquatorPositions.size();
        }

        if (freeOtherPositions.size() < otherGrassCount) {
            equatorGrassCount = Math.min(
                    equatorGrassCount + otherGrassCount - freeOtherPositions.size(),
                    equatorThickness * configuration.width()
            );
            otherGrassCount = freeOtherPositions.size();
        }

        // Dodaje trawe na równiku
        Collections.shuffle(freeEquatorPositions);
        for (int i = 0; i < equatorGrassCount; i++) {
            if (i >= freeEquatorPositions.size()) break; // Brak wolnych pozycji
            Vector2d position = freeEquatorPositions.get(i);
            grassMap.put(position, new Grass(position));
        }

        // Dodaje trawe poza równikiem
        Collections.shuffle(freeOtherPositions);
        for (int i = 0; i < otherGrassCount; i++) {
            if (i >= freeOtherPositions.size()) break; // Brak wolnych pozycji
            Vector2d position = freeOtherPositions.get(i);
            grassMap.put(position, new Grass(position));
        }
    }

    public void growInitialGrass() {
        growGrass(configuration.initialPlants());
    }

    public void dailyGrassGrowth(){
        growGrass(configuration.plantGrowthPerDay());
    }

    public void beforeEatUpdate(){}

    protected SortedSet<Animal> assureSetFor(Vector2d position) {
        SortedSet<Animal> animals = animalsMap.get(position);
        if (animals == null) {
            animalsMap.put(position, new TreeSet<>(new AnimalEnergyComparator()));
            return animalsMap.get(position);
        }
        return animals;
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

    public UUID getId() {
        return uuid;
    }

    public Map<Vector2d, Grass> getGrassMap() {
        return grassMap;
    }
    public Boundary getEquator() {
        return equator;
    }
    public Map<Vector2d, SortedSet<Animal>> getAnimalsMap() {
        return animalsMap;
    }
    public void mapChanged(AbstractMap worldMap, String message) {}
}
