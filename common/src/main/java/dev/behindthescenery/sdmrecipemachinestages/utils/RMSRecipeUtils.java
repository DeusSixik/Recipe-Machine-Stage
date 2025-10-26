package dev.behindthescenery.sdmrecipemachinestages.utils;

import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.api.RMSRecipeIdSupport;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class RMSRecipeUtils {

    public static final int CACHE_SIZE = 30;

    protected static final ThreadLocal<RecipeCache> LAST_GETTING_CACHE =
            ThreadLocal.withInitial(() -> new RecipeCache(CACHE_SIZE));

    private static Map<RecipeType<?>, RecipeHoldersKey> Recipes = new ConcurrentHashMap<>();

    public record RecipeHoldersKey(RecipeType<?> recipeType, Collection<RecipeValue<?>> recipes) { }

    public record RecipeValue<INPUT extends RecipeInput>(ResourceLocation id, Recipe<INPUT> recipe,
                                                         RecipeHolder<? extends Recipe<?>> recipeHolder) {
        @Override
        public @NotNull String toString() {
            return "RecipeValue{" +
                    "recipe=" + recipe.getType() +
                    ", id=" + id +
                    '}';
        }
    }

    @Nullable
    public static <INPUT extends RecipeInput> ResourceLocation getRecipeId(Recipe<INPUT> recipe) {
        final RecipeCache localCache = LAST_GETTING_CACHE.get();

        final @Nullable ResourceLocation id = localCache.find(recipe);
        if (id != null)
            return id;

        final RecipeHoldersKey holder = Recipes.get(recipe.getType());
        if (holder == null) return null;

        for (final RecipeValue<?> value : holder.recipes()) {
            if (value.recipe == recipe || Objects.equals(value.recipe, recipe)) {
                localCache.put(value);
                return value.id;
            }
        }

        return null;
    }

    public static RecipeValue<?> getRecipeData(Recipe<?> recipe) {
        final RecipeCache localCache = LAST_GETTING_CACHE.get();

        final @Nullable RecipeValue<?> id = localCache.findData(recipe);
        if (id != null)
            return id;

        final RecipeHoldersKey holder = Recipes.get(recipe.getType());
        if (holder == null) {
            RMSMain.LOGGER.error("Can't find recipeType {}. Registered recipes: {}", BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType()).toString(), Recipes.size());
            return null;
        }

        if(recipe instanceof RMSRecipeIdSupport support) {
            final ResourceLocation recipe_id = support.rms$getRecipeId();
            for (final RecipeValue<?> value : holder.recipes()) {
                if (value.id().equals(recipe_id)) {
                    localCache.put(value);
                    return value;
                }
            }
        } else {
            for (final RecipeValue<?> value : holder.recipes()) {
                if (value.recipe == recipe || Objects.equals(value.recipe, recipe)) {
                    localCache.put(value);
                    return value;
                }
            }
        }

        return null;
    }

    public static void reloadRecipeTypes(RecipeManager recipeManager) {
        reloadRecipeTypesAsync(recipeManager).join();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CompletableFuture<Map<RecipeType<?>, RecipeHoldersKey>> reloadRecipeTypesAsync(RecipeManager recipeManager) {
        final Map<RecipeType<?>, RecipeHoldersKey> newRecipes = new ConcurrentHashMap<>();
        final List<CompletableFuture<Void>> tasks = new ArrayList<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (final RecipeType<?> recipeType : BuiltInRegistries.RECIPE_TYPE) {
                final CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
                    final List<RecipeHolder<?>> recipes = (List<RecipeHolder<?>>)
                            (List<?>) recipeManager.getAllRecipesFor((RecipeType) recipeType);

                    if (!recipes.isEmpty()) {
                        final List<RecipeValue<?>> values = new ArrayList<>(recipes.stream()
                                .map(holder -> {
                                    final Recipe<?> recipe = holder.value();

                                    if(recipe instanceof RMSRecipeIdSupport idSupport) {
                                        idSupport.rms$setRecipeId(holder.id());
                                    }

                                    return new RecipeValue<>(holder.id(), recipe, holder);
                                })
                                .toList());

                        newRecipes.put(recipeType, new RecipeHoldersKey(recipeType, new ArrayList<>(values)));
                    }
                }, executor);
                tasks.add(task);
            }

            return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new))
                    .thenApply(v -> {
                        Recipes = newRecipes;
                        return newRecipes;
                    });
        }
    }

    private static class RecipeCache {
        private final RecipeValue<?>[] cache;
        private int index = 0;

        public RecipeCache(int size) {
            this.cache = new RecipeValue[size];
        }

        @Nullable
        public ResourceLocation find(Recipe<?> recipe) {
            if(recipe instanceof RMSRecipeIdSupport support)
                return support.rms$getRecipeId();

            for (RecipeValue<?> value : cache) {
                if (value == null) continue;
                if (value.recipe == recipe || Objects.equals(value.recipe, recipe))
                    return value.id;
            }
            return null;
        }

        @Nullable
        public RecipeValue<?> findData(Recipe<?> recipe) {

            if(recipe instanceof RMSRecipeIdSupport support) {
                final ResourceLocation recipe_id = support.rms$getRecipeId();
                for (RecipeValue<?> value : cache) {
                    if (value == null) continue;
                    if (value.id().equals(recipe_id))
                        return value;
                }
            } else {
                for (RecipeValue<?> value : cache) {
                    if (value == null) continue;
                    if (value.recipe == recipe || Objects.equals(value.recipe, recipe))
                        return value;
                }
            }

            return null;
        }

        public void put(RecipeValue<?> value) {
            cache[index] = value;
            index = (index + 1) % cache.length;
        }
    }

}
