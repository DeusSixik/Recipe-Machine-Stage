package net.sdm.recipemachinestage.api;

public enum RecipeRegisterType {

    /**
     * Restriction by {@link net.minecraft.world.item.crafting.RecipeType}
     */
    BY_RECIPE_TYPE,

    /**
     * Restriction by {@link net.minecraft.world.level.block.entity.BlockEntityType}
     */
    BY_BLOCK;

    public byte getId() {
        return (byte) ordinal();
    }
}
