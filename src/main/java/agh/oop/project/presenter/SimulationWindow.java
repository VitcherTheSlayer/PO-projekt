package agh.oop.project.presenter;

import agh.oop.project.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.List;

public class SimulationWindow implements MapChangeListener{
    private static final int CELL_SIZE_MIN = 3;
    private static final int CELL_SIZE_MAX = 50;
    private static final int MAX_AXES_CELL_SIZE = 13;

    private int cellSize = 10;
    private final Vector2d xDir = Rotation.EAST.nextMove();
    private final Vector2d yDir = Rotation.NORTH.nextMove();

    private Simulation simulation;
    private AbstractMap worldMap;
    private boolean simulationRunning = false;

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
    }

    public void init(Stage stage) {
        this.stage = stage;
    }

    public void setWorldMap(AbstractMap map){
        this.worldMap = map;
        map.addObserver(this);
        drawMap(); // Rysowanie mapy zaraz po ustawieniu
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

        // Dodanie obiektów mapy do siatki
        for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
            for (int y = lowerLeft.getY(); y <= upperRight.getY(); y++) {
                Vector2d position = new Vector2d(x, y);
                Object object = worldMap.objectAt(position);

                if (object != null && (int) object != 3) {
                    // Dodaj znaczek (Label) na pozycji, gdzie wykryto obiekt
                    Label objectLabel = new Label("X");  // Możesz zmienić "X" na dowolny znak, np. ">", "*"
                    if ((int) object == 1) {
                        objectLabel.setText("+");
                    } else if ((int) object == 2) {
                        objectLabel.setText("*");
                    }
                    objectLabel.setStyle("-fx-font-size: 18; -fx-text-fill: blue;"); // Stylizacja dla znaku

                    // Ustawienie pozycji na gridzie (liczba kolumn i wierszy)
                    GridPane.setHalignment(objectLabel, HPos.CENTER);
                    GridPane.setValignment(objectLabel, VPos.CENTER);

                    // Dodanie do mapy
                    mapGrid.add(objectLabel, x - lowerLeft.getX(), upperRight.getY() - y + 1);
                }
            }
        }
    }


    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0)); // Hack dla linii siatki
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    @Override
    public void mapChanged(AbstractMap worldMap, String message) {
        Platform.runLater(this::drawMap);
    }

}
