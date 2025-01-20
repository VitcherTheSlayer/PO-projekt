package agh.oop.project.presenter;

import agh.oop.project.model.Rotation;
import agh.oop.project.model.Simulation;
import agh.oop.project.model.Vector2d;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.swing.*;

public class SimulationWindow {
    private static final int CELL_SIZE_MIN = 3;
    private static final int CELL_SIZE_MAX = 50;
    private static final int MAX_AXES_CELL_SIZE = 13;

    private int cellSize = 10;
    private final Vector2d xDir = Rotation.EAST.nextMove();
    private final Vector2d yDir = Rotation.NORTH.nextMove();

    private Simulation simulation;
    private boolean simulationRunning = true;

    private int showAxis = 0;
    private Node lastNode = null;
    private boolean animalsSelectable = false;
    private Stage stage;

    @FXML
    private Label statisticsPresenter; // Do poprawki, narazie prowizorycznie
    @FXML
    private Label selectedAnimalPresenter; // Do poprawki, narazie prowizorycznie

    @FXML
    private GridPane gridPane;
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
    private void onClickStart(){
        if (!simulationRunning) {
            System.out.println("Overall simulation start");
            simulationRunning = true;
        }
    }
}
