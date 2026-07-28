package net.sdm.recipemachinestage.utils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.RecipeMachineStage;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Утилитарный класс содержащий в себе полезные методы для проверок и т.п
 */
public final class RMSUtils {

    private static RecipeManager recipeManager;

    public static void setRecipeManager(RecipeManager manager) {
        recipeManager = manager;
    }

    @NotNull
    public static RecipeManager getRecipeManager() {
        if(recipeManager == null) {
            throw new IllegalStateException("[RMS] RecipeManager is not set!");
        }

        return recipeManager;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }

    public static UUID getPlayerId(Player player) {
        return player.getGameProfile().getId();
    }

    public static boolean canCast(Class<?> obj, Class<?> caster){
        try {
            if(obj.equals(caster)) return true;

            List<Class<?>> d1 = getParent(obj);
            if(d1.contains(caster)) return true;

            d1 = getParent(caster);
            if(d1.contains(obj)) return true;
        } catch (Exception e){
            RecipeMachineStage.LOGGER.error("Error while casting", e);
        }
        return false;
    }

    public static ObjectArrayList<Class<?>> getParent(Class<?> obj){
        ObjectArrayList<Class<?>> classes = new ObjectArrayList<>();

        for (Class<?> anInterface : obj.getInterfaces()) {
            classes.add(anInterface);
            classes.addAll(getParent(anInterface));
        }

        if(obj.getSuperclass() != null){
            classes.add(obj.getSuperclass());
            classes.addAll(getParent(obj.getSuperclass()));
        }

        return classes;
    }

    public static Player getNearestPlayer(LevelAccessor level, BlockPos pos) {
        List<? extends Player> players = level.players();
        if(players.isEmpty()) return null;
        if(players.size() == 1) return players.get(0);

        Player nearestPlayer = null;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < players.size(); i++) {
            final Player player = players.get(i);
            final double distance = player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            if (distance < minDistance) {
                minDistance = distance;
                nearestPlayer = player;
            }
        }
        return nearestPlayer;
    }

    public static <RECIPE extends Recipe<?>> boolean canRecipe(@Nullable RECIPE recipe, BlockEntity block) {
        if(recipe == null || !StageContainer.hasRecipes(recipe.getType())) return true;

        final Optional<IOwnerBlock> capability = block.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        if(capability.isEmpty()) return true;

        final Level level = block.getLevel();
        final MinecraftServer server = level.getServer();
        if(server == null) return true;

        final IOwnerBlock ownerBlock = capability.get();
        final RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
        if(recipeBlockType == null) return true;

        final PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper
                .getPlayerByGameProfile(server, ownerBlock.getOwner());

        return player != null && player.hasStage(recipeBlockType.stage);
    }

    public static <RECIPE extends Recipe<?>> boolean canRecipe(RECIPE recipe, Player player) {
        return canRecipe(recipe, player.getServer(), player);
    }

    public static <RECIPE extends Recipe<?>> boolean canRecipe(RECIPE recipe, @Nullable MinecraftServer server, Player player) {
        return canRecipe(recipe, server, getPlayerId(player));
    }

    public static <RECIPE extends Recipe<?>> boolean canRecipe(RECIPE recipe, @Nullable MinecraftServer server, UUID playerId) {
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(recipe.getType())) return true;

        if(server == null)
            return true;

        final RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
        if(recipeBlockType == null) return true;

        final PlayerHelper.@Nullable RMSStagePlayerData playerData = PlayerHelper
                .getPlayerByGameProfile(server, playerId);

        if(playerData == null) return true;
        return playerData.hasStage(recipeBlockType.stage);
    }
}
