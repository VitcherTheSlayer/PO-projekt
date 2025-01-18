package agh.oop.project.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FullyRandomMutator implements IMutator {
    protected final int minMutation;
    protected final int maxMutation;
    protected final Random rng = new Random();

    protected List<Integer> makeRandomIdxs(int n) {
        List<Integer> genesIdxs = new ArrayList<>();
        for(int i = 0; i < n; i++){
            genesIdxs.add(i);
        }
        Collections.shuffle(genesIdxs, rng);
        return genesIdxs;
    }

    protected List<Integer> makeMutationIdxs(int maxIdx, int mutationCount) {
        return makeRandomIdxs(maxIdx)
                .stream()
                .limit(mutationCount)
                .toList();
    }

    public FullyRandomMutator(int minMutation, int maxMutation) {
        this.maxMutation = maxMutation;
        this.minMutation = minMutation;
    }

    protected Genome mutate(ArrayList<Integer> rawGenome){
        final int mutationCount = rng.nextInt(minMutation, maxMutation + 1);

        for(int i : makeMutationIdxs(rawGenome.size(), mutationCount)){
            rawGenome.set(i, rng.nextInt(Genome.UNIQUE_GENES_COUNT));
        }

        return new Genome(rawGenome);
    }

    @Override
    public Genome mutate(Genome genome) {
        return mutate(genome.copyValues());
    }
}
