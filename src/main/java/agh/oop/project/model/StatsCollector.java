package agh.oop.project.model;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StatsCollector {
    AbstractMap worldMap;
    // Lista wartości reprezentujących dni
    private final List<Integer> days = new ArrayList<>();

    // Zależności danych od dnia
    private final List<Integer> animalCounts = new ArrayList<>();
    private final List<Integer> plantCounts = new ArrayList<>();
    private final List<Integer> freeFields = new ArrayList<>();
    private final List<Double> averageEnergyLevels = new ArrayList<>();
    private final List<Double> averageLifespams = new ArrayList<>();

    public StatsCollector(AbstractMap worldMap) {
        this.worldMap = worldMap;
    }

    public void addData(AbstractMap worldMap,int day) {
        days.add(day);
        animalCounts.add(worldMap.getAnimals().size());
        plantCounts.add(worldMap.getPlantsAmount());
        freeFields.add(worldMap.getAllFreeCells());
        averageEnergyLevels.add(worldMap.getAverageEnergy());
        averageLifespams.add(worldMap.getAverageLifeSpan());
    }

    public void exportToCsv() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Simulation Stats");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(filter);

        fileChooser.setInitialDirectory(Paths.get(".").toFile());

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                // Nagłówki kolumn
                writer.write("Day,Animal Count,Plant Count,Free Fields,Average Energy,Average Lifespam\n");

                // Dane
                for (int i = 0; i < days.size(); i++) {
                    writer.write(String.format(
                            "%d,%d,%d,%d,%.2f,%.2f\n",
                            days.get(i),
                            animalCounts.get(i),
                            plantCounts.get(i),
                            freeFields.get(i),
                            averageEnergyLevels.get(i),
                            averageLifespams.get(i)
                    ));
                }
            }
        }
    }
}
