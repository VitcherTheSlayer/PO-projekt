package agh.oop.project.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StatisticsSaver {
    private final File file;

    private static final String dayLabel = "Day";
    private static final String animalCountLabel = "Animal Count";
    private static final String plantCountLabel = "Plant Count";
    private static final String freeFieldsLabel = "Free Fields";
    private static final String dominantGenomeLabel = "Dominant Genome";
    private static final String averageEnergyLabel = "Average Energy";
    private static final String averageLifespanLabel = "Average Lifespan";
    private static final String averageChildrenCountLabel = "Average Children Count";

    public StatisticsSaver(File file) throws IOException {
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

    public void saveDay(int day, Statistics stats) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.append("%d,%d,%d,%d,%s,%.3f,%.3f,%.3f\n".formatted(
                    day,
                    stats.animalCount(),
                    stats.plantCount(),
                    stats.freeFieldsCount(),
                    stats.dominantGenome().toString(),
                    stats.averageEnergy(),
                    stats.averageLifespan(),
                    stats.averageChildCount()
            ));
            fileWriter.flush();
        }
        catch (NullPointerException ignored) {}
    }
}
