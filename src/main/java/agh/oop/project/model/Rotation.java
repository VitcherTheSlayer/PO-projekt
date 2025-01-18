package agh.oop.project.model;

public enum Rotation {
    NORTH(0, new Vector2d(0, 1)),
    EAST(2, new Vector2d(1, 0)),
    SOUTH(4, new Vector2d(0, -1)),
    WEST(6, new Vector2d(-1, 0)),

    NORTHEAST(1, NORTH.nextMove().add(EAST.nextMove())),
    SOUTHEAST(3, SOUTH.nextMove().add(EAST.nextMove())),
    SOUTHWEST(5, SOUTH.nextMove().add(WEST.nextMove())),
    NORTHWEST(7, NORTH.nextMove().add(WEST.nextMove())),;

    private final int num;
    private final Vector2d nextMove;

    private Rotation(int num, Vector2d nextMove){
        this.num = num;
        this.nextMove = nextMove;
    }

    public static Rotation get(int n){
        n %= Genome.UNIQUE_GENES_COUNT;
        return switch (n) {
            case 0 -> NORTH;
            case 1 -> NORTHEAST;
            case 2 -> EAST;
            case 3 -> SOUTHEAST;
            case 4 -> SOUTH;
            case 5 -> SOUTHWEST;
            case 6 -> WEST;
            case 7 -> NORTHWEST;
            default -> throw new AssertionError("impossible, but compiler forced me to");
        };
    }

    public Rotation add(int n) {
        return get(num + n);
    }

    public Vector2d nextMove(){
        return nextMove;
    }
}
