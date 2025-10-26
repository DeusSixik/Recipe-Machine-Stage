package dev.behindthescenery.sdmrecipemachinestages.exceptions;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public class RecipeTypeNotSupported extends RuntimeException {

    public RecipeTypeNotSupported(String message, boolean isClass) {
        super(getMessage(message, isClass));
    }

    public RecipeTypeNotSupported(ResourceLocation location) {
        super(getMessage(location));
    }

    public RecipeTypeNotSupported(RecipeType<?> recipeType) {
        super(getMessage(recipeType));
    }

    protected static String getMessage(RecipeType<?> recipeType) {
        return getMessage(BuiltInRegistries.RECIPE_TYPE.getKey(recipeType));
    }

    protected static String getMessage(ResourceLocation location) {
        return getMessage(location.toString(), false);
    }

    protected static String getMessage(String str, boolean isClass) {
        if(isClass) {
            return "Class: " + str + " not supported!";
        }

        return "RecipeType: " + str + " not supported!";
    }
}
