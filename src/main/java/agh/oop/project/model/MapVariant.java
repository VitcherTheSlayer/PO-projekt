package agh.oop.project.model;

public enum MapVariant {
    GLOBE("Globe"),
    WILD_OWLBEAR("Wild Owlbear");

    public final String repr;

    MapVariant(String repr) {
        this.repr = repr;
    }

    @Override
    public String toString() {
        return repr;
    }

    public static MapVariant fromString(String repr) {
        switch (repr) {
            case "Globe": return GLOBE;
            case "Wild Owlbear": return WILD_OWLBEAR;
            default: return null;
        }
    }
}
