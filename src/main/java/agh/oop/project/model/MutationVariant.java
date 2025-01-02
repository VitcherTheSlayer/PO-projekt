package agh.oop.project.model;

public enum MutationVariant {
    FULL_RANDOMNESS("Full Randomness"),
    SWAP("Swap");

    public final String repr;

    MutationVariant(String repr) {
        this.repr = repr;
    }

    @Override
    public String toString() {
        return repr;
    }

    public static MutationVariant fromString(String repr) {
        switch (repr) {
            case "Full Randomness": return FULL_RANDOMNESS;
            case "Swap": return SWAP;
            default: return null;
        }
    }
}
