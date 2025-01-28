package agh.oop.project.model;

import java.io.*;

public record Configuration(
        int height,
        int width,
        MapVariant mapVariant,
        int initialPlants,
        int energyPerPlant,
        int plantGrowthPerDay,
        int initialAnimals,
        int initialEnergy,
        int reproductionMinEnergy,
        int reproductionEnergyUsage,
        int minMutations,
        int maxMutations,
        MutationVariant mutationVariant,
        int swapMutationPercent,
        int genomeLength,
        File csv
) implements Serializable {
    public Configuration {
        if(width <= 0){
            throw new IllegalArgumentException("width must be greater than 0");
        }
        if(width > 250){
            throw new IllegalArgumentException("width must be less than or equal to 250");
        }
        if(height <= 0){
            throw new IllegalArgumentException("height must be greater than 0");
        }
        if(height > 250){
            throw new IllegalArgumentException("height must be less than or equal to 250");
        }
        if(initialPlants < 0){
            throw new IllegalArgumentException("initialPlants must be greater or equal to 0");
        }
        if(energyPerPlant <= 0){
            throw new IllegalArgumentException("energyPerPlant must be greater than 0");
        }
        if(plantGrowthPerDay <= 0){
            throw new IllegalArgumentException("plantGrowthPerDay must be greater than 0");
        }
        if(initialAnimals <= 1){
            throw new IllegalArgumentException("initialAnimals must be greater than 1");
        }
        if(initialEnergy <= 0){
            throw new IllegalArgumentException("initialEnergy must be greater than 0");
        }
        if(reproductionMinEnergy < 0){
            throw new IllegalArgumentException("reproductionMinEnergy must be greater or equal to 0");
        }
        if(reproductionEnergyUsage < 0){
            throw new IllegalArgumentException("reproductionEnergyUsage must be greater or equal to 0");
        }
        if(minMutations < 0){
            throw new IllegalArgumentException("minMutations must be greater or equal to 0");
        }
        if(maxMutations < 0){
            throw new IllegalArgumentException("maxMutations must be greater or equal to 0");
        }
        if(genomeLength <= 0){
            throw new IllegalArgumentException("genomeLength must be greater than 0");
        }
        if(minMutations > genomeLength){
            throw new IllegalArgumentException("minMutations must be less than or equal to genomeLength");
        }
        if(maxMutations > genomeLength){
            throw new IllegalArgumentException("maxMutations must be less than or equal to genomeLength");
        }
        if(swapMutationPercent < 0){
            throw new IllegalArgumentException("swapMutationPercent must be greater than 0");
        }
        if (swapMutationPercent > 100){
            throw new IllegalArgumentException("swapMutationPercent must be less than or equal to 100");
        }
    }

    public static Configuration fromFile(File file) throws IOException, ClassNotFoundException {
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))){
            return (Configuration) in.readObject();
        }
    }

    public void toFile(File file) throws IOException {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))){
            out.writeObject(this);
        }
    }

    public static Configuration getDefault(){
        return new Configuration(
                10,
                10,
                MapVariant.GLOBE,
                30,
                2,
                5,
                7,
                10,
                7,
                3,
                1,
                5,
                MutationVariant.FULL_RANDOMNESS,
                50,
                10,
                null
        );

    }
}
