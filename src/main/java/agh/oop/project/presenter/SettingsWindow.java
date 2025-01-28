package agh.oop.project.presenter;

import agh.oop.project.model.*;
import agh.oop.project.presenter.SimulationWindow;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class SettingsWindow {
    @FXML
    private TextField heightTF;
    @FXML
    private TextField widthTF;
    @FXML
    private ComboBox<String> mapVariantCB;
    @FXML
    private TextField initialPlantsTF;
    @FXML
    private TextField energyPerPlantTF;
    @FXML
    private TextField plantGrowthPerDayTF;
    @FXML
    private TextField initialAnimalsTF;
    @FXML
    private TextField initialEnergyTF;
    @FXML
    private TextField reproductionMinEnergyTF;
    @FXML
    private TextField reproductionEnergyUsageTF;
    @FXML
    private TextField minMutationsTF;
    @FXML
    private TextField maxMutationsTF;
    @FXML
    private ComboBox<String> mutationVariantCB;
    @FXML
    private TextField swapMutationPercentTF;
    @FXML
    private TextField genomeLengthTF;

    @FXML
    private Button saveButton;

    @FXML
    private Button readButton;

    @FXML
    private Button startSimulationButton;

    private Stage stage;

    public void init(Stage stage) {
        this.stage = stage;

        mutationVariantCB.valueProperty().addListener((observable, oldValue, newValue) -> {
                swapMutationPercentTF.setDisable(!newValue.equals(MutationVariant.SWAP.toString()));
        });

        setConfig(Configuration.getDefault());
    }

    private void setConfig(Configuration config) {
        heightTF.setText(String.valueOf(config.height()));
        widthTF.setText(String.valueOf(config.width()));
        mapVariantCB.setValue(config.mapVariant().toString());
        initialPlantsTF.setText(String.valueOf(config.initialPlants()));
        energyPerPlantTF.setText(String.valueOf(config.energyPerPlant()));
        plantGrowthPerDayTF.setText(String.valueOf(config.plantGrowthPerDay()));
        initialAnimalsTF.setText(String.valueOf(config.initialAnimals()));
        initialEnergyTF.setText(String.valueOf(config.initialEnergy()));
        reproductionMinEnergyTF.setText(String.valueOf(config.reproductionMinEnergy()));
        reproductionEnergyUsageTF.setText(String.valueOf(config.reproductionEnergyUsage()));
        minMutationsTF.setText(String.valueOf(config.minMutations()));
        maxMutationsTF.setText(String.valueOf(config.maxMutations()));
        mutationVariantCB.setValue(config.mutationVariant().toString());
        swapMutationPercentTF.setText(String.valueOf(config.swapMutationPercent()));
        genomeLengthTF.setText(String.valueOf(config.genomeLength()));
    }

    private Configuration getConfig() {
        int height = Integer.parseInt(heightTF.getText());
        int width = Integer.parseInt(widthTF.getText());
        MapVariant mapVariant = MapVariant.fromString(mapVariantCB.getValue());
        int initialPlants = Integer.parseInt(initialPlantsTF.getText());
        int energyPerPlant = Integer.parseInt(energyPerPlantTF.getText());
        int plantGrowthPerDay = Integer.parseInt(plantGrowthPerDayTF.getText());
        int initialAnimals = Integer.parseInt(initialAnimalsTF.getText());
        int initialEnergy = Integer.parseInt(initialEnergyTF.getText());
        int reproductionMinEnergy = Integer.parseInt(reproductionMinEnergyTF.getText());
        int reproductionEnergyUsage = Integer.parseInt(reproductionEnergyUsageTF.getText());
        int minMutations = Integer.parseInt(minMutationsTF.getText());
        int maxMutations = Integer.parseInt(maxMutationsTF.getText());
        MutationVariant mutationVariant = MutationVariant.fromString(mutationVariantCB.getValue());
        int swapMutationPercent = Integer.parseInt(swapMutationPercentTF.getText());
        int genomeLength = Integer.parseInt(genomeLengthTF.getText());

        return new Configuration(
                height,
                width,
                mapVariant,
                initialPlants,
                energyPerPlant,
                plantGrowthPerDay,
                initialAnimals,
                initialEnergy,
                reproductionMinEnergy,
                reproductionEnergyUsage,
                minMutations,
                maxMutations,
                mutationVariant,
                swapMutationPercent,
                genomeLength
        );
    }

    @FXML
    private void save() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Config files (*.simconf)", "*.simconf");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setInitialDirectory(Paths.get(".").toFile());

        File file = fileChooser.showSaveDialog(stage);

        if(file != null){
            getConfig().toFile(file);
        }
    }
    @FXML
    private void read() throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Config files (*.simconf)", "*.simconf");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setInitialDirectory(Paths.get(".").toFile());

        File file = fileChooser.showOpenDialog(stage);

        if(file != null){
            setConfig(Configuration.fromFile(file));
        }
    }

    @FXML
    private void startSimulation() throws IOException {
        System.out.println("start Symulacji");

        // Załaduj nową scenę symulacji
        FXMLLoader simulationLoader = new FXMLLoader();
        simulationLoader.setLocation(getClass().getClassLoader().getResource("SimulationWindow.fxml"));
        AnchorPane simulationPane = simulationLoader.load();

        // Utwórz nową scenę i przypisz ją do Stage
        Scene simulationScene = new Scene(simulationPane);
        Stage simulationStage = new Stage();
        simulationStage.setScene(simulationScene);
        simulationStage.setTitle("Symulacja");
        simulationStage.setMaximized(true);
        simulationStage.setResizable(false);
        simulationStage.centerOnScreen();

        SimulationWindow simulationWindow = simulationLoader.getController();
        simulationWindow.init(simulationStage);
        Simulation simulation = SimulationFactory.createSimulation(this.getConfig());
        simulationWindow.setSimulation(simulation);
        simulationWindow.setWorldMap(simulation.getMap());
        simulation.createMapElements();

        simulationStage.show();
    }
}
