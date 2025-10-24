package dev.behindthescenery.sdmrecipemachinestages.utils;

import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockEntityCustomData;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockOwnerData;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.CustomData;
import dev.behindthescenery.sdmrecipemachinestages.data.RMSContainer;
import dev.behindthescenery.sdmrecipemachinestages.data.RecipeBlockType;
import dev.behindthescenery.sdmstages.StageApi;
import dev.behindthescenery.sdmstages.data.containers.Stage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RMSUtils {

    public static UUID getPlayerId(Player player) {
        return player.getGameProfile().getId();
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List<RecipeHolder<?>> getAllRecipesUnsafe(RecipeType<?> recipeType) {
        return (List<RecipeHolder<?>>) (List<?>) RMSMain.getRecipeManager().getAllRecipesFor((RecipeType) recipeType);
    }

    public static <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> filterRecipes(
            Collection<RecipeHolder<T>> original, Player player
    ) {
        return filterRecipes(original, getPlayerId(player));
    }

    public static <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> filterRecipes(
            Collection<RecipeHolder<T>> original, UUID player
    ) {
        final int size = original.size();
        if (size == 0) return new ArrayList<>(0);

        //TODO: May be use cache ?
        final List<RecipeHolder<T>> result = new ArrayList<>(size);
        for (final RecipeHolder<T> holder : original) {
            if (canProcess(player, holder)) {
                result.add(holder);
            }
        }
        return result;
    }

    @Nullable
    public static <T extends RecipeHolder<? extends Recipe<?>>> T ifCanProcessReturnRecipeOrNull(BlockEntity blockEntity, T recipe) {
        return canProcess(blockEntity, recipe) ? recipe : null;
    }

    public static boolean canProcess(BlockEntity blockEntity, RecipeHolder<? extends Recipe<?>> recipe) {
        if(recipe == null || blockEntity == null) return false;

        final RecipeBlockType blockType = getRecipeBlockData(recipe);
        if(blockType == null) return true;

        final CustomData customData = BlockEntityCustomData.getDataOrThrow(blockEntity);
        return hasPlayerStage(customData, blockType.stage_id());
    }

    public static boolean canProcess(Player player, RecipeHolder<? extends Recipe<?>> recipe) {
        return canProcess(getPlayerId(player), recipe);
    }

    public static boolean canProcess(UUID player, RecipeHolder<? extends Recipe<?>> recipe) {
        if(recipe == null || player == null) return false;
        final RecipeBlockType blockType = getRecipeBlockData(recipe);
        if(blockType == null) return true;
        return hasPlayerStage(player, blockType.stage_id());
    }

    public static boolean hasPlayerStage(CustomData customData, String stage_id) {
        return hasPlayerStage((UUID) customData.getData(BlockOwnerData.OWNER_KEY), stage_id);
    }

    public static boolean hasPlayerStage(Player player, String stage_id) {
        return hasPlayerStage(getPlayerId(player), stage_id);
    }

    public static boolean hasPlayerStage(UUID playerId, String stage_id) {
        final Stage stageData = RMSMain.getStageContainer().getStage(playerId);
        if(stageData == null) return false;
        return stageData.contains(stage_id);
    }


    @Nullable
    public static ServerPlayer getPlayerFromData(CustomData customData) {
        final UUID ownerId = customData.getData(BlockOwnerData.OWNER_KEY);

        for (ServerPlayer player : RMSMain.getServer().getPlayerList().getPlayers()) {
            if(getPlayerId(player).equals(ownerId))
                return player;
        }

        return null;
    }

    public static List<RecipeBlockType> getAllRecipesBlockData(RecipeHolder<? extends Recipe<?>> recipe) {
        return getAllRecipesBlockData(recipe.value());
    }

    public static List<RecipeBlockType> getAllRecipesBlockData(Recipe<?> recipe) {
        return getAllRecipesBlockData(recipe.getType());
    }

    public static List<RecipeBlockType> getAllRecipesBlockData(RecipeType<?> recipeType) {
        return RMSContainer.Instance.getRecipesBlock(recipeType);
    }

    @Nullable
    public static RecipeBlockType getRecipeBlockData(RecipeHolder<? extends Recipe<?>> recipe) {
        return getRecipeBlockData(recipe.value().getType(), recipe.id());
    }

    @Nullable
    public static RecipeBlockType getRecipeBlockData(RecipeType<?> recipeType, ResourceLocation recipeId) {
       return RMSContainer.Instance.getRecipeBlock(recipeType, recipeId).orElse(null);
    }


    public static Optional<RecipeBlockType> getRecipeBlockDataOptional(RecipeHolder<? extends Recipe<?>> recipe) {
        return getRecipeBlockDataOptional(recipe.value().getType(), recipe.id());
    }

    public static Optional<RecipeBlockType> getRecipeBlockDataOptional(RecipeType<?> recipeType, ResourceLocation recipeId) {
       return RMSContainer.Instance.getRecipeBlock(recipeType, recipeId);
    }

    public static void setBlockEntityOwner(BlockEntity entity, Player player) {
        setBlockEntityOwner(entity, getPlayerId(player));
    }

    public static void setBlockEntityOwner(BlockEntity entity, UUID owner_id) {
        if(!checkIfBockEntityCurrentType(entity)) return;
        BlockEntityCustomData.getDataOrThrow(entity).putData(BlockOwnerData.OWNER_KEY, owner_id);
    }

    public static boolean hasClientStage(String stage_id) {
        return StageApi.getClientStage().contains(stage_id);
    }

    public static boolean checkIfBockEntityCurrentType(BlockEntity block) {
        if(block instanceof RandomizableContainerBlockEntity) return false;
        return true;
    }
}
