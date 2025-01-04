package agh.oop.project.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SwapMutator extends FullyRandomMutator {

    private final int swapPercent;

    public SwapMutator(int minMutations, int maxMutations, int swapPercent) {
        super(minMutations, maxMutations);
        this.swapPercent = swapPercent;
    }

    protected Genome mutate(ArrayList<Integer> rawGenome){
        final int n = rng.nextInt(minMutation, maxMutation + 1);

        List<Integer> genesIdxs = new ArrayList<>();
        for(int i = 0; i < rawGenome.size(); i++){
            genesIdxs.add(i);
        }
        Collections.shuffle(genesIdxs, rng);
        int it = 0;

        for(int i = 0; i < n; i++){
            if (it >= genesIdxs.size() - 1) {
                it = 0;
                Collections.shuffle(genesIdxs, rng);
            }
            Collections.swap(rawGenome, genesIdxs.get(i), genesIdxs.get(i + 1));
            it += 2;
        }

        return new Genome(rawGenome);
    }

    @Override
    public Genome mutate(Genome genome) {
        if(rng.nextInt(100) < swapPercent){
            return mutate(genome.copyValues());
        }
        else{
            return super.mutate(genome);
        }
    }
}
