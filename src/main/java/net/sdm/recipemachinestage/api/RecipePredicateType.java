package net.sdm.recipemachinestage.api;


public enum RecipePredicateType {
    NONE;

    public byte getId() {
        return (byte) this.ordinal();
    }
}
