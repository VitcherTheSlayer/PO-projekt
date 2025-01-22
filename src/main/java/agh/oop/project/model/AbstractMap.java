package agh.oop.project.model;

import java.util.*;

public abstract class AbstractMap implements IMoveValidator,MapChangeListener {
    protected final Map<Vector2d, SortedSet<Animal>> animalsMap = new HashMap<>();
    protected final Map<Vector2d, Grass> grassMap = new HashMap<>(); // Jak objekt grass ma w sobie wektor to tutaj niepotrzebnie kopiuje się
    protected final Configuration configuration;
    protected final IMutator mutator;
    protected final Boundary boundary;
    private final List<Vector2d> preferedAreas = new ArrayList<>();
    private final List<Vector2d> notPreferedAreas = new ArrayList<>();
    private final Vector2d jungleLowerLeft;
    private final Vector2d jungleUpperRight;
    private final int height;
    private final int width;
    private final List<MapChangeListener> observers = new ArrayList<>();

    protected AbstractMap(Configuration configuration) {
        this.configuration = configuration;
        this.mutator = switch (configuration.mutationVariant()) {
            case SWAP -> new SwapMutator(configuration.minMutations(), configuration.maxMutations(), configuration.swapMutationPercent());
            case FULL_RANDOMNESS -> new FullyRandomMutator(configuration.minMutations(), configuration.maxMutations());
        };
        this.width = configuration.width();
        this.height = configuration.height();
        this.boundary = new Boundary(new Vector2d(0, 0), new Vector2d(this.width,this.height));

        this.jungleLowerLeft = new Vector2d((int) (this.width * 0.27),(int) (this.height * 0.27));
        this.jungleUpperRight = new Vector2d((int) (this.width * 0.73),(int) (this.height * 0.73));

        List<Vector2d> possiblePositions = new ArrayList<>();

        for (int x = 0; x < configuration.width(); x++) {
            for (int y = 0; y < configuration.height(); y++) {
                Vector2d position = new Vector2d(x, y);
                if (isWithinJungle(position)) {
                    preferedAreas.add(position);  // Pozycja w dżungli
                } else {
                    notPreferedAreas.add(position);  // Pozycja poza dżunglą
                }
            }
        }
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

    public List<Vector2d> getPreferedAreas() {
        return preferedAreas;
    }

    public List<Vector2d> getNotPreferedAreas() {
        return notPreferedAreas;
    }

    private boolean isWithinJungle (Vector2d position) {
        return position.precedes(jungleUpperRight) && position.follows(jungleLowerLeft);
    }

    public void placeAnimal (Animal animal) {
        makeSetFor(animal.getPosition());
        animalsMap.get(animal.getPosition()).add(animal);
        notifyObservers("Place Animal");
        System.out.println("Place Animal" + animal.getPosition());
    }

    public void placeGrass (Grass grass) {
        grassMap.put(grass.getPosition(), grass);
        notifyObservers("Place Grass");
        System.out.println("Place Grass " + grass.getPosition() );
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

    public void addObserver(MapChangeListener observer){
        observers.add(observer);
    }

    public void notifyObservers(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this, message);
        }
    }

    public void move(Animal animal,Vector2d oldPosition) {
        SortedSet<Animal> animalsAtOldPosition = animalsMap.get(oldPosition);

        if (animalsAtOldPosition != null) {

            animalsAtOldPosition.remove(animal);

            if (animalsAtOldPosition.isEmpty()) {
                animalsMap.remove(oldPosition);
            }
        }

        Vector2d newPosition = animal.getPosition(); // Nowa pozycja już ustawiona w Animal
        makeSetFor(newPosition);  // Upewnienie się, że istnieje zestaw
        SortedSet<Animal> animalsAtNewPosition = animalsMap.get(newPosition);
        boolean added = animalsAtNewPosition.add(animal);

        notifyObservers("Moved");
        System.out.println("Move Animal to " + newPosition);
    }


}
