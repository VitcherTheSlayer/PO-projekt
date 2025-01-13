package agh.oop.project.model.util;

import agh.oop.project.model.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomPositionGenerator implements Iterable<Vector2d> {
    private static class Iterator implements java.util.Iterator<Vector2d>{
        private final RandomPositionGenerator parent;

        public Iterator(RandomPositionGenerator parent){
            this.parent = parent;
        }

        @Override
        public boolean hasNext() {
            return parent.valid();
        }

        @Override
        public Vector2d next() {
            return parent.acquireNext();
        }
    }

    private final List<Vector2d> positions = new ArrayList<>();
    private final int size;
    private int count = 0;
    private final Random random = new Random();

    public RandomPositionGenerator(int maxWidth, int maxHeight, int count){
        if(count > (maxHeight + 1) * (maxWidth + 1)){
            throw new IllegalArgumentException();
        }

        this.size = count;

        for(int y = 0; y <= maxHeight; ++y){
            for(int x = 0; x <= maxWidth; ++x){
                positions.add(new Vector2d(x, y));
            }
        }
    }

    public Vector2d acquireNext() {
        if(!valid()){
            throw new IllegalStateException();
        }

        // 1st call:
        // [0][1][2][3][4][5][6][7]|
        //        ^- idx
        // 2nd call:
        // [0][1][7][3][4][5][6]|[2]
        //           ^- idx
        int idx = random.nextInt(positions.size() - count);
        ++count;

        // 1st call:
        // [0][1][7][3][4][5][6]|[2]
        //        ^- idx          ^- return
        // 2nd call:
        // [0][1][7][6][4][5]|[3][2]
        //           ^- idx    ^- return
        Collections.swap(positions, idx, positions.size() - count);
        return positions.get(positions.size() - count);
    }

    public boolean valid(){
        return count != size;
    }

    @Override
    public java.util.Iterator<Vector2d> iterator() {
        return new Iterator(this);
    }
}
