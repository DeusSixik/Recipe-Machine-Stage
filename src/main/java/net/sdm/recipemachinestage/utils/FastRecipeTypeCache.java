package net.sdm.recipemachinestage.utils;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.sdm.recipemachinestage.RMSConstants;
import net.sdm.recipemachinestage.impl.patches.BlockEntityTypeIndex;
import net.sdm.recipemachinestage.impl.patches.RecipeTypeIndex;

public final class FastRecipeTypeCache {

    private volatile static boolean initialized;

    public static RecipeType<?>[] RECIPE_TYPES;
    public static BlockEntityType<?>[] BLOCK_ENTITY_TYPES;

    private static int size;

    public static void init() {
        Registry<RecipeType<?>> register = BuiltInRegistries.RECIPE_TYPE;

        int registrySize = register.size();
        if (registrySize <= 1) {
            return;
        }

        if (initialized && size == registrySize && RECIPE_TYPES != null && BLOCK_ENTITY_TYPES != null) {
            return;
        }

        synchronized (FastRecipeTypeCache.class) {
            register = BuiltInRegistries.RECIPE_TYPE;
            registrySize = register.size();
            if (registrySize <= 1) {
                return;
            }

            if (initialized && size == registrySize && RECIPE_TYPES != null && BLOCK_ENTITY_TYPES != null) {
                return;
            }

            RecipeType<?>[] recipeTypes = new RecipeType[registrySize];
            for (RecipeType<?> recipeType : register) {
                int fastId = register.getId(recipeType);
                if (fastId < 0 || fastId >= size) {
                    continue;
                }
                recipeTypes[fastId] = recipeType;
                RecipeTypeIndex.get(recipeType).gpr$setIndex(fastId);
            }

            Registry<BlockEntityType<?>> blockEntityTypeRegistry = BuiltInRegistries.BLOCK_ENTITY_TYPE;

            BlockEntityType<?>[] blockEntityTypes = new BlockEntityType[blockEntityTypeRegistry.size()];
            for (BlockEntityType<?> blockEntityType : blockEntityTypeRegistry) {
                int fastId = blockEntityTypeRegistry.getId(blockEntityType);
                if (fastId < 0 || fastId >= size) {
                    continue;
                }
                blockEntityTypes[fastId] = blockEntityType;
                BlockEntityTypeIndex.get(blockEntityType).gpr$setIndex(fastId);
            }

            RECIPE_TYPES = recipeTypes;
            BLOCK_ENTITY_TYPES = blockEntityTypes;
            size = registrySize;

            RMSConstants.LOGGER.info("RECIPE_TYPES REGISTRY SIZE: {}", size);
            initialized = true;
        }
    }

    public static BlockEntityType<?> getBlockEntityType(int id) {
        if (id < 0) {
            return null;
        }
        BlockEntityType<?>[] states = BLOCK_ENTITY_TYPES;
        if (states == null) {
            init();
            states = BLOCK_ENTITY_TYPES;
        }
        if (states == null || id >= states.length) {
            return BuiltInRegistries.BLOCK_ENTITY_TYPE.byId(id);
        }
        return states[id];
    }

    public static RecipeType<?> getRecipeType(int id) {
        if (id < 0) {
            return null;
        }
        RecipeType<?>[] states = RECIPE_TYPES;
        if (states == null) {
            init();
            states = RECIPE_TYPES;
        }
        if (states == null || id >= states.length) {
            return BuiltInRegistries.RECIPE_TYPE.byId(id);
        }

        return states[id];
    }

    public static int getSize() {
        return size;
    }
}
