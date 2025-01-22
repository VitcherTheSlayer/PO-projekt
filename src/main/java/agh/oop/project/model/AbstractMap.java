package agh.oop.project.model;

import agh.oop.project.model.util.RandomPositionGenerator;

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
        notifyObservers("Place Animal");
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
                if(second.breedable()){
                    addAnimal(first.breedWith(second));
                }
            }
        }
    }

    protected void growGrass(int count) {
        // "... Istnieje 80% szansy, że nowa roślina wyrośnie na preferowanym polu,
        //  a tylko 20% szans, że wyrośnie na polu drugiej kategorii ..."

        int equatorGrassCount = count * EQUATOR_GROWTH_PERCENT / 100;
        int otherGrassCount = count - equatorGrassCount;

        RandomPositionGenerator equatorGenerator = new RandomPositionGenerator(
                configuration.width(),
                equatorThickness,
                equatorGrassCount,
                grassMap.keySet()::contains
        );

        RandomPositionGenerator otherGenerator = new RandomPositionGenerator(
                configuration.width(),
                configuration.height(),
                otherGrassCount,
                pos -> equator.test(pos) || grassMap.containsKey(pos)
        );

        for(Vector2d pos : equatorGenerator){
            grassMap.put(pos.add(equatorDelta), new Grass(pos));
        }
        for(Vector2d pos : otherGenerator){
            grassMap.put(pos, new Grass(pos));
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
            return animalsMap.put(position, new TreeSet<>(new AnimalEnergyComparator()));
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

    public int objectAt(Vector2d position) { // Prowizorka, sprawdzam czy coś jest wgl
        SortedSet<Animal> animalsAtPosition = animalsMap.get(position);
        if (animalsAtPosition != null) {
            int len = animalsMap.get(position).size();
            if (len > 0) {
                return 1;
            }
            } else {
            if (grassMap.containsKey(position)){
                return 2;
            }
        }

        return 3;
    }
    public UUID getId() {
        return uuid;
    }
}
