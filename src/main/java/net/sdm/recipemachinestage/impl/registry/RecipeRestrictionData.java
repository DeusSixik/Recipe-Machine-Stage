package net.sdm.recipemachinestage.impl.registry;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.sdm.recipemachinestage.api.RecipeRegisterType;
import net.sdm.recipemachinestage.impl.patches.RecipeManagerExtern;
import net.sdm.recipemachinestage.impl.patches.RecipeTypeIndex;
import net.sdm.recipemachinestage.utils.RMSUtils;

import java.util.Arrays;

/**
 * Stores compact recipe restriction data for a stage-gated rule.
 *
 * <p>The {@code recipeData} array uses a packed layout with repeating groups:
 * {@code [recipeTypeIndex, recipeCount, recipeId1, recipeId2, ...]}.
 *
 * @param registerType the restriction type, see {@link RecipeRegisterType}
 * @param stage the progression stage that restricts the recipes
 * @param blockId the block id for {@link RecipeRegisterType#BY_BLOCK} restrictions,
 *                or {@value #NO_BLOCK_ID} when the restriction is not block-bound
 * @param recipeData packed recipe restriction data
 */
public record RecipeRestrictionData(
        byte registerType,
        short stage,
        int blockId,
        int[] recipeData
) {
    /**
     * Sentinel value used when the restriction is not bound to a block id.
     */
    public static final int NO_BLOCK_ID = -1;

    private static final int GROUP_HEADER_SIZE = 2;

    /**
     * Creates a new record using the enum-based register type API.
     *
     * @param registerType the restriction type
     * @param stage the stage that restricts the recipes
     * @param blockId the block id for block-based restrictions
     * @param recipeData packed recipe restriction data
     */
    public RecipeRestrictionData(RecipeRegisterType registerType, short stage, int blockId, int[] recipeData) {
        this(registerType.getId(), stage, blockId, recipeData);
    }

    /**
     * Creates a block-based restriction and auto-groups recipes by their type.
     *
     * @param stage the stage that restricts the recipes
     * @param blockId the restricted block id
     * @param recipes recipe indices to pack
     * @return a new packed restriction record
     */
    public static RecipeRestrictionData createByBlock(short stage, int blockId, int[] recipes) {
        return new RecipeRestrictionData(RecipeRegisterType.BY_BLOCK, stage, blockId, packData(recipes));
    }

    /**
     * Creates a block-based restriction for a single known recipe type.
     *
     * @param stage the stage that restricts the recipes
     * @param blockId the restricted block id
     * @param recipeType the recipe type index
     * @param recipes recipe indices to pack
     * @return a new packed restriction record
     */
    public static RecipeRestrictionData createByBlock(short stage, int blockId, int recipeType, int[] recipes) {
        return new RecipeRestrictionData(RecipeRegisterType.BY_BLOCK, stage, blockId, packData(recipeType, recipes));
    }

    /**
     * Creates a recipe-type-based restriction and auto-groups recipes by their type.
     *
     * @param stage the stage that restricts the recipes
     * @param recipes recipe indices to pack
     * @return a new packed restriction record
     */
    public static RecipeRestrictionData createByRecipeType(short stage, int[] recipes) {
        return new RecipeRestrictionData(RecipeRegisterType.BY_RECIPE_TYPE, stage, NO_BLOCK_ID, packData(recipes));
    }

    /**
     * Creates a recipe-type-based restriction for a single known recipe type.
     *
     * @param stage the stage that restricts the recipes
     * @param recipeType the recipe type index
     * @param recipes recipe indices to pack
     * @return a new packed restriction record
     */
    public static RecipeRestrictionData createByRecipeType(short stage, int recipeType, int[] recipes) {
        return new RecipeRestrictionData(RecipeRegisterType.BY_RECIPE_TYPE, stage, NO_BLOCK_ID, packData(recipeType, recipes));
    }

    /**
     * Packs recipe indices that all belong to the same recipe type.
     *
     * @param recipeType the recipe type index
     * @param recipes recipe indices to pack
     * @return packed data in the format {@code [recipeType, count, recipes...]}
     */
    public static int[] packData(int recipeType, int[] recipes) {
        final int count = recipes.length;
        final int[] flatData = new int[count + GROUP_HEADER_SIZE];

        flatData[0] = recipeType;
        flatData[1] = count;
        System.arraycopy(recipes, 0, flatData, GROUP_HEADER_SIZE, count);
        return flatData;
    }

    /**
     * Packs recipe indices by grouping them by recipe type.
     *
     * <p>The resulting groups are sorted by recipe type index so the packed
     * representation is deterministic for the same recipe set.
     *
     * @param recipes recipe indices to pack
     * @return packed recipe data
     */
    public static int[] packData(int[] recipes) {
        RecipeManagerExtern managerExtern = RecipeManagerExtern.get(RMSUtils.getRecipeManager());
        Int2ObjectMap<IntArrayList> groupedRecipes = new Int2ObjectOpenHashMap<>();

        for (int i = 0; i < recipes.length; i++) {
            final int recipeId = recipes[i];
            final Recipe<?> recipe = managerExtern.getRecipeByIndex(recipeId);
            final RecipeType<?> type = recipe.getType();
            final int typeIndex = RecipeTypeIndex.get(type).gpr$getIndex();
            groupedRecipes.computeIfAbsent(typeIndex, ignored -> new IntArrayList()).add(recipeId);
        }

        final int totalSize = (groupedRecipes.size() * GROUP_HEADER_SIZE) + recipes.length;
        final int[] flatData = new int[totalSize];
        final int[] sortedTypeIndices = groupedRecipes.keySet().toIntArray();
        int pointer = 0;

        Arrays.sort(sortedTypeIndices);

        for (int typeIndex : sortedTypeIndices) {
            IntArrayList list = groupedRecipes.get(typeIndex);
            int count = list.size();

            flatData[pointer++] = typeIndex;
            flatData[pointer++] = count;
            list.getElements(0, flatData, pointer, count);
            pointer += count;
        }

        return flatData;
    }

    /**
     * Unpacks this record's packed recipe data.
     *
     * @return a map of {@code recipeTypeIndex -> recipeIndices}
     * @throws IllegalArgumentException if the packed layout is malformed
     */
    public Int2ObjectMap<int[]> unpackData() {
        return unpackData(recipeData);
    }

    /**
     * Unpacks packed recipe data into groups keyed by recipe type index.
     *
     * @param packedData packed recipe data
     * @return a map of {@code recipeTypeIndex -> recipeIndices}
     * @throws IllegalArgumentException if the packed layout is malformed
     */
    public static Int2ObjectMap<int[]> unpackData(int[] packedData) {
        Int2ObjectMap<int[]> unpacked = new Int2ObjectOpenHashMap<>();
        int pointer = 0;

        while (pointer < packedData.length) {
            if (pointer + GROUP_HEADER_SIZE > packedData.length) {
                throw new IllegalArgumentException("Malformed packed recipe data: missing group header");
            }

            int recipeType = packedData[pointer++];
            int recipeCount = packedData[pointer++];

            if (recipeCount < 0) {
                throw new IllegalArgumentException("Malformed packed recipe data: negative recipe count");
            }

            if (pointer + recipeCount > packedData.length) {
                throw new IllegalArgumentException("Malformed packed recipe data: group exceeds array bounds");
            }

            int[] recipeIds = new int[recipeCount];
            System.arraycopy(packedData, pointer, recipeIds, 0, recipeCount);
            unpacked.put(recipeType, recipeIds);
            pointer += recipeCount;
        }

        return unpacked;
    }
}
