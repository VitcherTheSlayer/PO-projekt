package agh.oop.project.presenter;

import agh.oop.project.model.*;
import agh.oop.project.model.AbstractMap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import java.util.Map;
import java.util.SortedSet;
import java.util.*;
import java.util.concurrent.Semaphore;

public class SimulationWindow {
    private static final int CELL_SIZE_MIN = 3;
    private static final int CELL_SIZE_MAX = 50;
    private static final int MAX_AXES_CELL_SIZE = 13;

    private int cellSize = 10;
    private final Vector2d xDir = Rotation.EAST.nextMove();
    private final Vector2d yDir = Rotation.NORTH.nextMove();

    private Simulation simulation;
    private AbstractMap worldMap;
    private boolean simulationRunning = false;
    private Vector2d owlbearPosition;

    private int showAxis = 0;
    private Node lastNode = null;
    private boolean animalsSelectable = false;
    private Stage stage;

    @FXML
    private Label statisticsPresenter; // Do poprawki, narazie prowizorycznie
    @FXML
    private Label selectedAnimalPresenter; // Do poprawki, narazie prowizorycznie

    @FXML
    private GridPane mapGrid;
    @FXML
    private ToggleButton pauseButton;
    @FXML
    private ToggleButton resumeButton;
    @FXML
    private ToggleButton startButton;
    @FXML
    private Label simulationStatusLabel;

    @FXML
    private void onClickPlay() {
        if (!simulationRunning) {
            resumeButton.setSelected(true);
            pauseButton.setSelected(false);
            System.out.println("simulation.resume();");
            simulationRunning = true;
//          simulation.resume();
        }

    }

    @FXML
    private void onClickPause() {
        if (simulationRunning) {
            pauseButton.setSelected(true);
            resumeButton.setSelected(false);
            System.out.println("simulation.pause();");
            simulationRunning = false;
//          simulation.pause();
        }

    }

    @FXML
    public void onClickStart() {
        System.out.println("simulation.start();");
        if (!simulationRunning) {
            simulationRunning = true;
            Thread simulationThread = new Thread(() -> {
                try {
                    simulation.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            simulationThread.setDaemon(true);
            simulationThread.start();
        }
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        this.simulation.setSimulationWindow(this);
    }

    public void init(Stage stage) {
        this.stage = stage;
    }

    public void setWorldMap(AbstractMap map){
        this.worldMap = map;
    }

    public void drawMapReforged() {
        if (worldMap == null || mapGrid == null) return;
        clearGrid();

        Boundary bounds = worldMap.getBoundary();
        Vector2d lowerLeft = bounds.lowerLeft();
        Vector2d upperRight = bounds.upperRight();

        for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(50));
        }
        for (int y = lowerLeft.getY(); y <= upperRight.getY(); y++) {
            mapGrid.getRowConstraints().add(new RowConstraints(50));
        }

        // 0.Pobieram obrazki
        Image Desert = new Image(getClass().getResourceAsStream("/images/Desert.png"));
        Image Jungle = new Image(getClass().getResourceAsStream("/images/Jungle.png"));
        Image Palm = new Image(getClass().getResourceAsStream("/images/Palm.png"));
        Image RedArea = new Image(getClass().getResourceAsStream("/images/RedArea.png"));


        // 1. Rysuje tło z elementami mapy owlbear
        for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
            for (int y = lowerLeft.getY(); y <= upperRight.getY(); y++) {
                Vector2d position = new Vector2d(x, y);

                // Sprawdzenie, czy pozycja znajduje się w granicach jungli
                Image backgroundImage = worldMap.getEquator().test(position) ? Jungle : Desert;

                // Tworzenie ImageView dla tła
                ImageView backgroundView = new ImageView(backgroundImage);

                // Dopasowanie rozmiaru
                backgroundView.setFitWidth(50);
                backgroundView.setFitHeight(50);

                // Ustawienie w gridzie
                mapGrid.add(backgroundView, x - lowerLeft.getX(), y - lowerLeft.getY()); // Zmieniona pozycja Y

                // Dodanie prostokąta z współrzędnymi w prawym górnym rogu
                Label coordinatesLabel = new Label(x + "," + y);
                coordinatesLabel.setStyle("-fx-font-size: 10; -fx-text-fill: white; -fx-background-color: rgba(0, 0, 0, 0.5);");
                GridPane.setHalignment(coordinatesLabel, HPos.RIGHT); // Ustawienie do prawego górnego rogu
                GridPane.setValignment(coordinatesLabel, VPos.BOTTOM);   // Ustawienie do prawego górnego rogu
                mapGrid.add(coordinatesLabel, x - lowerLeft.getX(), y - lowerLeft.getY()); // Dodanie do siatki

                // Sprawdzamy, czy mapą jest OwlbearMap
                if (worldMap instanceof OwlbearMap) {
                    OwlbearMap map = (OwlbearMap) worldMap;
                    if (map.getHuntingGround().test(position)) {
                        // Jeśli jest to instancja OwlbearMap, dodajemy RedArea
                        ImageView redAreaView = new ImageView(RedArea);
                        redAreaView.setFitWidth(50);
                        redAreaView.setFitHeight(50);

                        // Ustawiamy pozycję RedArea
                        mapGrid.add(redAreaView, x - lowerLeft.getX(), y - lowerLeft.getY());
                    }

                    // Rysowanie Owlbeara
                    Owlbear owlbear = map.getOwlbear();
                    this.owlbearPosition = owlbear.getPosition();

                    if (position.equals(owlbearPosition)) {
                        Image owlbearImage = new Image(getClass().getResourceAsStream("/images/Owlbear/" + owlbear.getRotation() + ".png"));
                        ImageView owlbearImageView = new ImageView(owlbearImage);
                        owlbearImageView.setFitWidth(50);
                        owlbearImageView.setFitHeight(50);

                        // Ustawienie pozycji dla Owlbeara
                        mapGrid.add(owlbearImageView, x - lowerLeft.getX(), y - lowerLeft.getY());
                    }
                }
            }
        }

        // 2. Rysuje animale
        for (Map.Entry<Vector2d, SortedSet<Animal>> entry : worldMap.getAnimalsMap().entrySet()) {
            Vector2d position = entry.getKey();
            SortedSet<Animal> animalsAtPosition = entry.getValue();

            if (!animalsAtPosition.isEmpty()) {
                // Dla każdego zwierzęcia na tej pozycji
                for (Animal animal : animalsAtPosition) {
                    if (animal == null) continue;
                    // Tworzymy odpowiednią ścieżkę do obrazka na podstawie kierunku
                    String animalDirection = animal.getRotation().name();  // Zgodnie z enum Rotation
                    Vector2d animalPosition = animal.getPosition();

                    // Budujemy ścieżkę do obrazka zwierzęcia
                    Image animalImage = new Image(getClass().getResourceAsStream("/images/Animal/" + animalDirection + ".png"));

                    // Tworzymy ImageView, aby wyświetlić obrazek
                    ImageView animalImageView = new ImageView(animalImage);

                    // Dopasowanie rozmiaru obrazka
                    animalImageView.setFitWidth(50);  // Możesz dostosować wymiary
                    animalImageView.setFitHeight(50);

                    // Przekształcenie współrzędnych na siatkę
                    int gridX = animalPosition.getX();
                    int gridY = animalPosition.getY();

                    // Ustawienie pozycji na siatce
                    GridPane.setHalignment(animalImageView, HPos.CENTER);
                    GridPane.setValignment(animalImageView, VPos.CENTER);

                    // Dodanie do siatki
                    mapGrid.add(animalImageView, gridX, gridY);

                    // Rysujemy tylko jednego zwierzaka z danego miejsca
                    break;
                }
            }
        }

        // 3. Rysuje trawe
        for (Map.Entry<Vector2d, Grass> entry : worldMap.getGrassMap().entrySet()) {
            Vector2d position = entry.getKey();

            // Jeśli pozycja nie jest równa pozycji Owlbeara
            if (!position.equals(owlbearPosition)) {
                // Tworzenie ImageView dla trawy
                ImageView grassImageView = new ImageView(Palm);

                // Dopasowanie rozmiaru obrazka
                grassImageView.setFitWidth(50);
                grassImageView.setFitHeight(50);

                // Przekształcenie współrzędnych na siatkę
                int gridX = position.getX();
                int gridY = position.getY();

                // Ustawienie wyrównania
                GridPane.setHalignment(grassImageView, HPos.CENTER);
                GridPane.setValignment(grassImageView, VPos.CENTER);

                // Dodanie obrazka do siatki
                mapGrid.add(grassImageView, gridX, gridY);
            }
        }
    }


    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0)); // Hack dla linii siatki
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    public void mapChanged(Semaphore semaphore) {
        Platform.runLater(() -> {
            drawMapReforged(); // Rysowanie mapy
            updateSelectedAnimalPresenter(); // Aktualizacja etykiety z pozycjami zwierząt (debug)
            semaphore.release();
        });
    }

    // Aktualizacja etykiety `selectedAnimalPresenter` z aktualnymi pozycjami zwierząt
    private void updateSelectedAnimalPresenter() {
        if (worldMap == null || selectedAnimalPresenter == null) return;

        // Pobierz listę wszystkich zwierząt
        List<Animal> animals = worldMap.getAnimals();

        // Jeśli lista jest pusta, wyświetl odpowiedni komunikat
        if (animals.isEmpty()) {
            selectedAnimalPresenter.setText("No animals on the map.");
            return;
        }

        // Budowanie tekstu z pozycjami zwierząt
        StringBuilder positionsBuilder = new StringBuilder("Current Animal Positions:\n");
        animals.forEach(animal -> {
            positionsBuilder.append("Position: ").append(animal.getPosition())
                    .append(", Direction: ").append(animal.getRotation())
                    .append("\n");
        });

        // Ustawienie tekstu w etykiecie
        selectedAnimalPresenter.setText(positionsBuilder.toString());
    }
}