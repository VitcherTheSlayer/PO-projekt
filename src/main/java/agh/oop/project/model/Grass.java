package agh.oop.project.model;

public class Grass implements IMapEntity {
    private final Vector2d position;

    public Grass(Vector2d position){
        this.position = position;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }
}
