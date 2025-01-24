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
                simulation.run();
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


    public void drawMap() {
        if (worldMap == null || mapGrid == null) return;
        clearGrid();

        Boundary bounds = worldMap.getBoundary();
        Vector2d lowerLeft = bounds.lowerLeft();
        Vector2d upperRight = bounds.upperRight();

        // Dodanie rozmiarów kolumn i wierszy
        for (int x = lowerLeft.getX(); x <= upperRight.getX() + 1; x++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(50));
        }
        for (int y = lowerLeft.getY(); y <= upperRight.getY() + 1; y++) {
            mapGrid.getRowConstraints().add(new RowConstraints(50));
        }

        // Dodanie współrzędnych X do pierwszego wiersza (rząd[0])
        for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
            Label labelX = new Label(String.valueOf(x));
            GridPane.setHalignment(labelX, HPos.CENTER);
            mapGrid.add(labelX, x - lowerLeft.getX(), 0);
        }

        // Dodanie współrzędnych Y do ostatniej kolumny (kolumna[11] - po prawej stronie)
        for (int y = lowerLeft.getY(); y <= upperRight.getY(); y++) {
            Label labelY = new Label(String.valueOf(y));
            GridPane.setHalignment(labelY, HPos.CENTER);
            GridPane.setValignment(labelY, VPos.CENTER);
            mapGrid.add(labelY, upperRight.getX() - lowerLeft.getX() + 1, upperRight.getY() - y + 1);
        }

        Label labelXY = new Label("X/Y");
        GridPane.setHalignment(labelXY, HPos.CENTER);
        GridPane.setValignment(labelXY, VPos.CENTER);
        mapGrid.add(labelXY, upperRight.getX() - lowerLeft.getX() + 1, 0);

        Image Death = new Image(getClass().getResourceAsStream("/images/Death.png"));
        Image Desert = new Image(getClass().getResourceAsStream("/images/Desert.png"));
        Image Jungle = new Image(getClass().getResourceAsStream("/images/Jungle.png"));
        Image Palm = new Image(getClass().getResourceAsStream("/images/Palm.png"));
        Image RedArea = new Image(getClass().getResourceAsStream("/images/RedArea.png"));

        Map<Rotation, Image> animalImages = new HashMap<>();
        Map<Rotation, Image> owlbearImages = new HashMap<>();

        // Ładowanie obrazków
        for (Rotation rotation : Rotation.values()) {
            // Dla zwierząt
            String animalPath = String.format("/images/Animal/%s.png", rotation.name());
            animalImages.put(rotation, new Image(getClass().getResourceAsStream(animalPath)));

            // Dla Owlbear
            String owlbearPath = String.format("/images/Owlbear/%s.png", rotation.name());
            owlbearImages.put(rotation, new Image(getClass().getResourceAsStream(owlbearPath)));
        }

        //0. Rysowanie tła + elementy OwlbearMap
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
                GridPane.setHalignment(backgroundView, HPos.CENTER);
                GridPane.setValignment(backgroundView, VPos.CENTER);
                mapGrid.add(backgroundView, x - lowerLeft.getX(), upperRight.getY() - y + 1);

                // Sprawdzamy, czy mapą jest OwlbearMap
                if (worldMap instanceof OwlbearMap) {
                    // Jeśli jest to instancja OwlbearMap, dodajemy RedArea
                    OwlbearMap map = (OwlbearMap) worldMap;
                    if (map.getHuntingGround().test(position)) {
                        ImageView redAreaView = new ImageView(RedArea);
                        redAreaView.setFitWidth(50);
                        redAreaView.setFitHeight(50);
//                        redAreaView.setOpacity(0.5);  // Ustawienie przezroczystości

                        // Ustawiamy pozycję RedArea
                        GridPane.setHalignment(redAreaView, HPos.CENTER);
                        GridPane.setValignment(redAreaView, VPos.CENTER);
                        mapGrid.add(redAreaView, x - lowerLeft.getX(), upperRight.getY() - y + 1);

                        // Rysowanie Owlbeara
                        Owlbear owlbear = map.getOwlbear();
                        this.owlbearPosition = owlbear.getPosition();

                        if (position.equals(owlbearPosition)) {
                            // Załaduj odpowiedni obrazek Owlbeara w zależności od jego rotacji
                            Image owlbearImage = new Image(getClass().getResourceAsStream("/images/Owlbear/" + owlbear.getRotation() + ".png"));
                            ImageView owlbearImageView = new ImageView(owlbearImage);
                            owlbearImageView.setFitWidth(50);  // Możesz dostosować szerokość
                            owlbearImageView.setFitHeight(50); // Możesz dostosować wysokość

                            // Ustawienie pozycji dla Owlbeara
                            GridPane.setHalignment(owlbearImageView, HPos.CENTER);
                            GridPane.setValignment(owlbearImageView, VPos.CENTER);

                            // Dodanie do siatki
                            mapGrid.add(owlbearImageView, x - lowerLeft.getX(), upperRight.getY() - y + 1);
                        }
                    }
                }
            }
        }


        // 1. Rysowanie zwierząt
        for (Map.Entry<Vector2d, SortedSet<Animal>> entry : worldMap.getAnimalsMap().entrySet()) {
            Vector2d position = entry.getKey();
            SortedSet<Animal> animalsAtPosition = entry.getValue();

            if (!animalsAtPosition.isEmpty()) {
                // Dla każdego zwierzęcia na tej pozycji
                for (Animal animal : animalsAtPosition) {
                    // Tworzymy odpowiednią ścieżkę do obrazka na podstawie kierunku
                    String animalDirection = animal.getRotation().name();  // Zgodnie z enum Rotation

                    // Budujemy ścieżkę do obrazka zwierzęcia
                    Image animalImage = new Image(getClass().getResourceAsStream("/images/Animal/" + animalDirection + ".png"));

                    // Tworzymy ImageView, aby wyświetlić obrazek
                    ImageView animalImageView = new ImageView(animalImage);

                    // Dopasowanie rozmiaru obrazka
                    animalImageView.setFitWidth(50);  // Możesz dostosować wymiary
                    animalImageView.setFitHeight(50);

                    // Ustawienie pozycji na siatce
                    GridPane.setHalignment(animalImageView, HPos.CENTER);
                    GridPane.setValignment(animalImageView, VPos.CENTER);

                    // Dodanie do siatki
                    mapGrid.add(animalImageView, position.getX() - lowerLeft.getX(), upperRight.getY() - position.getY() + 1);
                }
            }
        }


        // 2. Rysowanie trawy
        for (Map.Entry<Vector2d, Grass> entry : worldMap.getGrassMap().entrySet()) {
            Vector2d position = entry.getKey();
            if (!position.equals(owlbearPosition)) {
                // Tworzenie ścieżki do obrazka trawy
                Image grassImage = new Image(getClass().getResourceAsStream("/images/Palm.png"));

                // Tworzenie ImageView, aby wyświetlić obrazek
                ImageView grassImageView = new ImageView(Palm);

                // Dopasowanie rozmiaru obrazka (opcjonalnie dostosuj wymiary)
                grassImageView.setFitWidth(50);  // Możesz dostosować szerokość
                grassImageView.setFitHeight(50); // Możesz dostosować wysokość

                // Ustawienie pozycji na siatce
                GridPane.setHalignment(grassImageView, HPos.CENTER);
                GridPane.setValignment(grassImageView, VPos.CENTER);

                // Dodanie do siatki
                mapGrid.add(grassImageView, position.getX() - lowerLeft.getX(), upperRight.getY() - position.getY() + 1);
            }
        }
    }


    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0)); // Hack dla linii siatki
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    public void mapChanged() {
        Platform.runLater(this::drawMap);
    }

}
