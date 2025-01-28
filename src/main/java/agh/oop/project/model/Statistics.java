package agh.oop.project.model;

public record Statistics(
        int animalCount,
        int plantCount,
        int freeFieldsCount,
        Genome dominantGenome,
        double averageEnergy,
        double averageLifespan,
        double averageChildCount
) {
}
