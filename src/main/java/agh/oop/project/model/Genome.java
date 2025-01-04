package agh.oop.project.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Genome implements Iterable<Integer> {
    private static class GenomeIterator implements Iterator<Integer>{
        private final Genome genome;
        private int i;

        public GenomeIterator(Genome genome, int i) {
            this.genome = genome;
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            // cyclic iterator
            return true;
        }

        @Override
        public Integer next() {
            var result = genome.get(i);
            i = (i + 1) % Genome.UNIQUE_GENES_COUNT;
            return result;
        }
    }

    public static final int UNIQUE_GENES_COUNT = 8;

    private final ArrayList<Integer> values = new ArrayList<>();

    public Genome(int n) {
        var rng = new Random();
        for(int i = 0; i < n; i++) {
            values.add(rng.nextInt(UNIQUE_GENES_COUNT));
        }
    }

    public Genome(List<Integer> value) {
        for(int gene : value){ // deep copy
            values.add(gene);
        }
    }

    public int get(int i) {
        return values.get(i);
    }

    public ArrayList<Integer> copyValues() {
        var result = new ArrayList<Integer>();
        for(int gene : values){ // deep copy
            result.add(gene);
        }
        return result;
    }

    public int size() {
        return values.size();
    }

    @Override
    public Iterator<Integer> iterator() {
        return new GenomeIterator(this, 0);
    }

    public Iterator<Integer> iterator(int i) {
        return new GenomeIterator(this, i);
    }
}
