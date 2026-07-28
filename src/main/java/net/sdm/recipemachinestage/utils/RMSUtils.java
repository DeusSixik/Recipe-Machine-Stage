package net.sdm.recipemachinestage.utils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.LevelAccessor;
import net.sdm.recipemachinestage.RecipeMachineStage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
}
