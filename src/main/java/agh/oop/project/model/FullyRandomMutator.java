package agh.oop.project.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FullyRandomMutator implements Mutator {

    protected final int minMutation;
    protected final int maxMutation;
    protected final Random rng = new Random();

    public FullyRandomMutator(int minMutation, int maxMutation) {
        this.maxMutation = maxMutation;
        this.minMutation = minMutation;
    }

    protected Genome mutate(ArrayList<Integer> rawGenome){
        final int n = rng.nextInt(minMutation, maxMutation + 1);

        List<Integer> genesIdxs = new ArrayList<>();
        for(int i = 0; i < rawGenome.size(); i++){
            genesIdxs.add(i);
        }
        Collections.shuffle(genesIdxs, rng);

        for(int i : genesIdxs
                .stream()
                .limit(n)
                .toList()){
            rawGenome.set(i, rng.nextInt(Genome.UNIQUE_GENES_COUNT));
        }

        return new Genome(rawGenome);
    }

    @Override
    public Genome mutate(Genome genome) {
        return mutate(genome.copyValues());
    }
}
