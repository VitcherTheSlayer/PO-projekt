package agh.oop.project.model.util;

import agh.oop.project.model.Boundary;
import agh.oop.project.model.Vector2d;

import java.util.*;
import java.util.function.Predicate;

public class RandomPositionGenerator implements Iterable<Vector2d> {
    private class Iterator implements java.util.Iterator<Vector2d>{
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

    private List<Vector2d> positions = new ArrayList<>();
    private final int size;
    private int count = 0;
    private final Random random = new Random();

    public RandomPositionGenerator(int maxWidth, int maxHeight, int count){
        this(maxWidth, maxHeight, count, null);
    }

    public RandomPositionGenerator(int maxWidth, int maxHeight, int count, Predicate<Vector2d> exclusionPredicate){
        if(count > (maxHeight + 1) * (maxWidth + 1)){
            throw new IllegalArgumentException();
        }

        this.size = count;

        for(int y = 0; y <= maxHeight; ++y){
            for(int x = 0; x <= maxWidth; ++x){
                if(exclusionPredicate != null && !exclusionPredicate.test(new Vector2d(x, y))){
                    positions.add(new Vector2d(x, y));
                }
                else if(exclusionPredicate == null){
                    positions.add(new Vector2d(x, y));
                }
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

    public static Vector2d getRandomPosition(int maxWidth, int maxHeight) {
        Random random = new Random();
        int x = random.nextInt(maxWidth + 1);
        int y = random.nextInt(maxHeight + 1);
        return new Vector2d(x, y);
    }

    public static Vector2d getRandomPositionWithinBounds(Boundary bounds) {
        Random rng = new Random();

        int minX = bounds.lowerLeft().getX();
        int minY = bounds.lowerLeft().getY();
        int maxX = bounds.upperRight().getX();
        int maxY = bounds.upperRight().getY();

        int randomX = rng.nextInt(minX,maxX + 1);
        int randomY = rng.nextInt(minY,maxY + 1);

        return new Vector2d(randomX, randomY);
    }

}