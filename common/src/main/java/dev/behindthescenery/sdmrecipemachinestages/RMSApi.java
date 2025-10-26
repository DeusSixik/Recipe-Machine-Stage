package dev.behindthescenery.sdmrecipemachinestages;

import dev.behindthescenery.sdmrecipemachinestages.data.*;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class RMSApi {

    public static void addRecipe(String recipeType, ResourceLocation recipeID, String stage) {
        RMSApi.register(BuiltInRegistries.RECIPE_TYPE.get(ResourceLocation.tryParse(recipeType)), new ArrayList<>(List.of(recipeID)), stage);
    }

    public static void addRecipe(String recipeType, ResourceLocation[] recipeID, String stage) {
        RMSApi.register(BuiltInRegistries.RECIPE_TYPE.get(ResourceLocation.tryParse(recipeType)), new ArrayList<>(List.of(recipeID)), stage);
    }

    public static void addRecipe(RecipeType<? extends Recipe<? extends RecipeInput>> recipeType, ResourceLocation recipeID, String stage) {
        RMSApi.register(recipeType, new ArrayList<>(List.of(recipeID)), stage);
    }

    public static void addRecipe(RecipeType<? extends Recipe<? extends RecipeInput>> recipeType, ResourceLocation[] recipeID, String stage) {
        RMSApi.register(recipeType, new ArrayList<>(List.of(recipeID)), stage);
    }

    public static void addRecipeByMod(String recipeType, String modId, String stage) {
        addRecipeByMod(BuiltInRegistries.RECIPE_TYPE.get(ResourceLocation.tryParse(recipeType)), modId, stage);
    }

    public static void addRecipeByMod(RecipeType<?> recipeType, String modId, String stage) {
        List<ResourceLocation> recipeIds = RMSUtils.getAllRecipesUnsafe(recipeType)
                .stream()
                .map(RecipeHolder::id)
                .filter(id -> id.getNamespace().equals(modId))
                .toList();

        RMSApi.register(recipeType, new ArrayList<>(recipeIds), stage);
    }

    public static void addRecipeByMod(String recipeType, String[] modId, String stage) {
        addRecipeByMod(BuiltInRegistries.RECIPE_TYPE.get(ResourceLocation.tryParse(recipeType)), modId, stage);
    }

    public static void addRecipeByMod(RecipeType<?> recipeType, String[] modIds, String stage) {
        List<ResourceLocation> recipeIds = RMSUtils.getAllRecipesUnsafe(recipeType)
                .stream()
                .map(RecipeHolder::id)
                .filter(id -> {
                    for (String s : modIds) {
                        if (id.getNamespace().equals(s)) return true;
                    }
                    return false;
                })
                .toList();

        RMSApi.register(recipeType, new ArrayList<>(recipeIds), stage);
    }

    public static void register(RecipeType<?> recipeType, List<ResourceLocation> recipeName, String stage) {
        RMSContainer.Instance.register(new RecipeBlockType(stage, recipeType, recipeName));
    }

    public static void register(Class<?> blockClass, List<ItemStack> inputs, String stage, RecipeBlockClass type) {
        switch (type) {
            case Input -> RMSContainer.Instance.register(new RecipeBlockInput(blockClass, stage, inputs));
            case Out -> RMSContainer.Instance.register(new RecipeBlockOutput(blockClass, stage, inputs));
        }
    }

    public static void printAllRecipes(ResourceLocation recipeTypeId, Consumer<String> onGet) {
        printAllRecipes(BuiltInRegistries.RECIPE_TYPE.get(recipeTypeId), onGet);
    }

    public static void printAllRecipes(RecipeType<?> recipeType, Consumer<String> onGet) {
        if(RMSMain.getServer() == null || recipeType == null) return;

        onGet.accept("Start dump recipes: ");
        for (RecipeHolder<?> recipeHolder : RMSUtils.getAllRecipesUnsafe(recipeType)) {
            onGet.accept(recipeHolder.id().toString());
        }
    }

    public static enum RecipeBlockClass {
        Input,
        Out
    }
}
