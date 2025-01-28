package agh.oop.project.model;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StatsCollector {
    private final File file;

    private static final String dayLabel = "Day";
    private static final String animalCountLabel = "Animal Count";
    private static final String plantCountLabel = "Plant Count";
    private static final String freeFieldsLabel = "Free Fields";
    private static final String dominantGenomeLabel = "Dominant Genome";
    private static final String averageEnergyLabel = "Average Energy";
    private static final String averageLifespanLabel = "Average Lifespan";
    private static final String averageChildrenCountLabel = "Average Children Count";

    public StatsCollector(File file) throws IOException {
        this.file = file;

        try(FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("%s,%s,%s,%s,%s,%s,%s,%s\n".formatted(
                    dayLabel,
                    animalCountLabel,
                    plantCountLabel,
                    freeFieldsLabel,
                    dominantGenomeLabel,
                    averageEnergyLabel,
                    averageLifespanLabel,
                    averageChildrenCountLabel
            ));
            fileWriter.flush();
        }
        catch (NullPointerException ignored) {}
    }
}
