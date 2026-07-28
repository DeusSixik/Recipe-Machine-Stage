package net.sdm.recipemachinestage.impl.registry;

import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortLinkedOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.sdm.recipemachinestage.RMSConstants;
import net.sdm.recipemachinestage.api.RecipeRegisterType;
import net.sdm.recipemachinestage.api.restriction_collector.RecipeRestrictionRawData;
import net.sdm.recipemachinestage.impl.patches.BlockEntityTypeIndex;
import net.sdm.recipemachinestage.impl.patches.RecipeManagerExtern;
import net.sdm.recipemachinestage.impl.patches.RecipeTypeIndex;
import net.sdm.recipemachinestage.impl.patches.RecipeTypeSupport;
import net.sdm.recipemachinestage.utils.RMSUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Класс регистра. Он хранит в себе все ограничения
 */
public final class RMSRegistry {

    public static final RMSRegistry REGISTRY = new RMSRegistry();

    private short[] knowStages;
    private Int2ShortOpenHashMap recipeByStage = new Int2ShortOpenHashMap();
    private final Long2ObjectMap<RecipeRestrictionData> restrictions = new Long2ObjectLinkedOpenHashMap<>();
    private final ObjectOpenHashSet<RecipeRestrictionRawData> pendingData = new ObjectOpenHashSet<>();

    public short[] getKnowStages() {
        return knowStages;
    }

    public short getStageByRecipe(int recipe) {
        return recipeByStage.get(recipe);
    }

    public short[] getRequiredStages(int recipeIndex, @Nullable BlockEntityType<?> blockType,
                                     @Nullable RecipeType<?> recipeType) {
        if (recipeIndex < 0 || knowStages == null || knowStages.length == 0) {
            return ShortArrays.EMPTY_ARRAY;
        }

        ShortLinkedOpenHashSet stages = new ShortLinkedOpenHashSet();

        if (recipeType != null) {
            for (short stageId : knowStages) {
                RecipeRestrictionData restriction = getRestriction(createKey(recipeType, stageId));
                if (containsRecipe(restriction, recipeIndex)) {
                    stages.add(stageId);
                }
            }
        }

        if (blockType != null) {
            for (short stageId : knowStages) {
                RecipeRestrictionData restriction = getRestriction(createKey(RecipeRegisterType.BY_BLOCK, blockType, stageId));
                if (containsRecipe(restriction, recipeIndex)) {
                    stages.add(stageId);
                }
            }
        }

        return stages.toShortArray();
    }

    public void addPendingData(RecipeRestrictionRawData data) {
        pendingData.add(data);
    }

    public @Nullable RecipeRestrictionData getRestriction(long key) {
        return restrictions.get(key);
    }

    public Long2ObjectMap<RecipeRestrictionData> getRestrictions() {
        return restrictions;
    }

    public List<RecipeRestrictionData> createRestrictionSnapshot() {
        return List.copyOf(restrictions.values());
    }

    public static long createKey(RecipeRegisterType type, BlockEntity blockEntity, short stage) {
        return createKey(type, blockEntity.getType(), stage);
    }

    public static long createKey(RecipeRegisterType type, BlockEntityType<?> blockEntityType, short stage) {
        return createKey(type.getId(), BlockEntityTypeIndex.get(blockEntityType).gpr$getIndex(), stage);
    }

    public static long createKey(RecipeType<?> recipeType, short stage) {
        return createKey(RecipeRegisterType.BY_RECIPE_TYPE.getId(), RecipeTypeIndex.get(recipeType).gpr$getIndex(), stage);
    }

    public static long createKey(byte registerType, int blockId, short stage) {
        return (((long) registerType & 0xFFL) << 48) |
                (((long) stage & 0xFFFFL) << 32) |
                ((long) blockId & 0xFFFFFFFFL);
    }

    public void registerData() {
        restrictions.clear();

        ShortList knowStagesList = new ShortArrayList();
        Int2ShortOpenHashMap recipeByStage = new Int2ShortOpenHashMap();
        recipeByStage.defaultReturnValue((short) -1);
        try {
            RecipeManagerExtern managerExtern = RecipeManagerExtern.get(RMSUtils.getRecipeManager());
            Long2ObjectMap<IntLinkedOpenHashSet> mergedRecipes = new Long2ObjectLinkedOpenHashMap<>();
            IntOpenHashSet restrictedTypeIndices = new IntOpenHashSet();

            for (RecipeRestrictionRawData pending : pendingData) {
                short stageId = resolveStageId(pending.stage());
                knowStagesList.add(stageId);
                int blockTargetId = pending.registerType() == RecipeRegisterType.BY_BLOCK
                        ? resolveBlockTargetId(pending)
                        : RecipeRestrictionData.NO_BLOCK_ID;
                int declaredRecipeTypeId = resolveDeclaredRecipeTypeId(pending);

                for (ResourceLocation recipeId : pending.recipes()) {
                    int recipeIndex = managerExtern.getRecipeIndex(recipeId);
                    if (recipeIndex < 0) {
                        String errorMessage = "Unknown recipe id '" + recipeId + "'";
                        RMSConstants.LOGGER.error(errorMessage);
//                        NeoForge.EVENT_BUS.post(new RegisterRestrictionErrorEvent(errorMessage));
                        return;
                    }

                    managerExtern.getRecipeByIndex(recipeIndex);
                    Recipe<?> holder = managerExtern.getRecipeByIndex(recipeIndex);
                    if (holder == null) {
                        String errorMessage = "Missing recipe holder for recipe index " + recipeIndex;
                        RMSConstants.LOGGER.error(errorMessage);
//                        NeoForge.EVENT_BUS.post(new RegisterRestrictionErrorEvent(errorMessage));
                        return;
                    }

                    int recipeTypeIndex = RecipeTypeIndex.get(holder.getType()).gpr$getIndex();
                    if (declaredRecipeTypeId != RecipeRestrictionData.NO_BLOCK_ID && declaredRecipeTypeId != recipeTypeIndex) {
                        String errorMessage = "Recipe '" + recipeId + "' has type index " + recipeTypeIndex +
                                ", but restriction declared type index " + declaredRecipeTypeId;
                        RMSConstants.LOGGER.error(errorMessage);
//                        NeoForge.EVENT_BUS.post(new RegisterRestrictionErrorEvent(errorMessage));
                        return;
                    }

                    int targetId = pending.registerType() == RecipeRegisterType.BY_BLOCK
                            ? blockTargetId
                            : recipeTypeIndex;
                    long key = createKey(pending.registerType().getId(), targetId, stageId);
                    IntLinkedOpenHashSet recipeIds = mergedRecipes.computeIfAbsent(key, ignored -> new IntLinkedOpenHashSet());

                    restrictedTypeIndices.add(recipeTypeIndex);
                    if (pending.registerType() == RecipeRegisterType.BY_RECIPE_TYPE) {
                        recipeByStage.put(recipeIndex, stageId);
                    }
                    recipeIds.add(recipeIndex);
                }
            }

            this.recipeByStage = recipeByStage;
            this.knowStages = knowStagesList.toShortArray();
            buildRestrictions(mergedRecipes);
            applyRecipeTypesSupports(restrictedTypeIndices);

            RMSConstants.LOGGER.info("RMS: Restrictions registered. Recipes: {}, Types: {}", mergedRecipes.size(), restrictedTypeIndices.size());
        } finally {
            pendingData.clear();
        }
    }

    public void clearData() {
        pendingData.clear();
        restrictions.clear();
        recipeByStage = new Int2ShortOpenHashMap();
        recipeByStage.defaultReturnValue((short) -1);
        knowStages = null;
    }

    private void applyRecipeTypesSupports(IntOpenHashSet restrictedTypeIndices) {
        for (RecipeType<?> recipeType : BuiltInRegistries.RECIPE_TYPE) {
            if (recipeType instanceof RecipeTypeSupport support) {
                support.gpr$setHasRestrictions(
                        restrictedTypeIndices.contains(RecipeTypeIndex.get(recipeType).gpr$getIndex())
                );
            }
        }
    }

    private void buildRestrictions(Long2ObjectMap<IntLinkedOpenHashSet> mergedRecipes) {
        LongArrayList sortedKeys = new LongArrayList(mergedRecipes.keySet());
        LongArrays.quickSort(sortedKeys.elements(), 0, sortedKeys.size());

        for (int i = 0; i < sortedKeys.size(); i++) {
            long key = sortedKeys.getLong(i);
            byte registerTypeId = getRegisterTypeId(key);
            short stageId = getStageId(key);
            int targetId = getTargetId(key);
            int[] recipeIds = mergedRecipes.get(key).toIntArray();

            IntArrays.quickSort(recipeIds);

            RecipeRestrictionData restriction = switch (RecipeRegisterType.values()[registerTypeId]) {
                case BY_BLOCK -> new RecipeRestrictionData(
                        RecipeRegisterType.BY_BLOCK,
                        stageId,
                        targetId,
                        RecipeRestrictionData.packData(recipeIds)
                );
                case BY_RECIPE_TYPE -> new RecipeRestrictionData(
                        RecipeRegisterType.BY_RECIPE_TYPE,
                        stageId,
                        RecipeRestrictionData.NO_BLOCK_ID,
                        RecipeRestrictionData.packData(targetId, recipeIds)
                );
            };

            restrictions.put(key, restriction);
        }
    }

    private short resolveStageId(Object rawStage) {
        if (rawStage instanceof Short stageId) {
            return stageId;
        }

        if (rawStage instanceof Integer stageId) {
            if (stageId < Short.MIN_VALUE || stageId > Short.MAX_VALUE) {
                throw new IllegalArgumentException("Stage id is out of short range: " + stageId);
            }

            return stageId.shortValue();
        }

        if (rawStage instanceof String stageName) {
            return StageIndex.getOrCreate(stageName);
        }

        throw new IllegalArgumentException("Unsupported stage object: " + rawStage);
    }

    private int resolveBlockTargetId(RecipeRestrictionRawData pending) {
        BlockEntityType<?> blockType = pending.block();
        if (blockType == null) {
            throw new IllegalArgumentException("Block restriction requires a block entity type");
        }

        return BlockEntityTypeIndex.get(blockType).gpr$getIndex();
    }

    private int resolveDeclaredRecipeTypeId(RecipeRestrictionRawData pending) {
        if (pending.registerType() != RecipeRegisterType.BY_RECIPE_TYPE) {
            return RecipeRestrictionData.NO_BLOCK_ID;
        }

        RecipeType<?> recipeType = pending.recipeType();
        return recipeType == null
                ? RecipeRestrictionData.NO_BLOCK_ID
                : RecipeTypeIndex.get(recipeType).gpr$getIndex();
    }

    private static byte getRegisterTypeId(long key) {
        return (byte) ((key >>> 48) & 0xFFL);
    }

    private static short getStageId(long key) {
        return (short) ((key >>> 32) & 0xFFFFL);
    }

    private static int getTargetId(long key) {
        return (int) key;
    }

    private static boolean containsRecipe(@Nullable RecipeRestrictionData restriction, int recipeIndex) {
        if (restriction == null) {
            return false;
        }

        int[] packedData = restriction.recipeData();
        int pointer = 0;

        while (pointer < packedData.length) {
            pointer++;
            int recipeCount = packedData[pointer++];

            for (int i = 0; i < recipeCount; i++) {
                if (packedData[pointer + i] == recipeIndex) {
                    return true;
                }
            }

            pointer += recipeCount;
        }

        return false;
    }

    private static long createRestrictionKey(RecipeRestrictionData restriction) {
        if (restriction.registerType() == RecipeRegisterType.BY_BLOCK.getId()) {
            return createKey(restriction.registerType(), restriction.blockId(), restriction.stage());
        }

        int[] packedData = restriction.recipeData();
        int recipeTypeIndex = packedData.length == 0 ? RecipeRestrictionData.NO_BLOCK_ID : packedData[0];
        return createKey(restriction.registerType(), recipeTypeIndex, restriction.stage());
    }

    private static int[] flattenRestrictionRecipes(RecipeRestrictionData restriction) {
        IntLinkedOpenHashSet recipeIds = new IntLinkedOpenHashSet();
        int[] packedData = restriction.recipeData();
        int pointer = 0;

        while (pointer < packedData.length) {
            pointer++;
            int recipeCount = packedData[pointer++];

            for (int i = 0; i < recipeCount; i++) {
                recipeIds.add(packedData[pointer + i]);
            }

            pointer += recipeCount;
        }

        return recipeIds.toIntArray();
    }
}
