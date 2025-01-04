package agh.oop.project.model;

import java.util.ArrayList;
import java.util.Random;

public class GenomeCombiner {
    public static Genome combine(Genome strongerGenome, Genome weakerGenome, int strongerEnergy, int weakerEnergy) {
        ArrayList<Integer> rawGenome = new ArrayList<>();

        if(strongerEnergy < weakerEnergy){ // ensure proper naming
            Genome tempGenome = strongerGenome;
            strongerGenome = weakerGenome;
            weakerGenome = tempGenome;

            int tempInt = strongerEnergy;
            strongerEnergy = weakerEnergy;
            weakerEnergy = tempInt;
        }

        int genesCount = (int) ((double)weakerEnergy / (double)(strongerEnergy + weakerEnergy) * (double)Genome.UNIQUE_GENES_COUNT);

        if(new Random().nextBoolean()){ // take weaker as first
            for(int i = 0; i < genesCount; i++){
                rawGenome.add(weakerGenome.get(i));
            }
            for(int i = genesCount; i < weakerGenome.size(); i++){
                rawGenome.add(strongerGenome.get(i));
            }
        }
        else{ // take stronger as first
            genesCount = weakerGenome.size() - genesCount;
            for(int i = 0; i < genesCount; i++){
                rawGenome.add(strongerGenome.get(i));
            }
            for(int i = genesCount; i < weakerGenome.size(); i++){
                rawGenome.add(weakerGenome.get(i));
            }
        }

        return new Genome(rawGenome);
    }
}
