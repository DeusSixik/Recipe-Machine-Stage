package dev.behindthescenery.sdmrecipemachinestages.neoforge.utils;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import team.lodestar.lodestone.systems.recipe.LodestoneRecipeType;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RMSLodeStoneUtils {

    public static <T extends RecipeInput, K extends Recipe<T>> K getRecipe(Level level, RecipeType<K> recipeType, T recipeInput, UUID ownerId) {
        return findRecipe(level, recipeType, (recipe) -> recipe.matches(recipeInput, level), ownerId);
    }

    public static <T extends RecipeInput, K extends Recipe<T>> K findRecipe(Level level, RecipeType<K> recipeType, Predicate<K> predicate, UUID ownerId) {
        for(RecipeHolder<K> recipe : RMSUtils.filterRecipes(LodestoneRecipeType.getRecipeHolders(level, recipeType), ownerId)) {
            K value = recipe.value();
            if (predicate.test(value)) {
                return value;
            }
        }

        return null;
    }

    public static <T extends RecipeInput, K extends Recipe<T>> K getRecipeDistance(Level level, RecipeType<K> recipeType, T recipeInput, BlockEntity entity) {
        return findRecipeDistance(level, recipeType, (recipe) -> recipe.matches(recipeInput, level), entity);
    }

    public static <T extends RecipeInput, K extends Recipe<T>> K findRecipeDistance(Level level, RecipeType<K> recipeType, Predicate<K> predicate, BlockEntity entity) {
        List<RecipeHolder<K>> recipes;

        if(!RMSUtils.hasRestrictionsForType(recipeType)) {
            recipes = LodestoneRecipeType.getRecipeHolders(level, recipeType);
        } else {

            final int distance = 20;
            final BlockPos pos = entity.getBlockPos();
            final int x = pos.getX();
            final int y = pos.getY();
            final int z = pos.getZ();

            final List<Player> player = entity.getLevel().getEntitiesOfClass(Player.class, new AABB(x - distance, y - distance, z - distance, x + distance, y + distance, z + distance));
            if(!player.isEmpty()) {
                recipes = RMSUtils.filterRecipes(LodestoneRecipeType.getRecipeHolders(level, recipeType), RMSUtils.getPlayerId(player.getFirst()));
            } else return null;
        }

        for(RecipeHolder<K> recipe : recipes) {
            K value = recipe.value();
            if (predicate.test(value)) {
                return value;
            }
        }

        return null;
    }

    public static <T extends RecipeInput, K extends Recipe<T>> List<K> getRecipes(Level level, RecipeType<K> recipeType, UUID ownerId) {
        return getRecipeHolders(level, recipeType, ownerId).stream().map(RecipeHolder::value).collect(Collectors.toList());
    }

    public static <T extends RecipeInput, K extends Recipe<T>> List<RecipeHolder<K>> getRecipeHolders(Level level, RecipeType<K> recipeType, UUID ownerId) {
        return RMSUtils.filterRecipes(level.getRecipeManager().getAllRecipesFor(recipeType), ownerId);
    }
}
