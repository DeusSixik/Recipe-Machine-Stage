package dev.behindthescenery.sdmrecipemachinestages.supported;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RMSSupportedTypes {

    private static final List<String> SupportedTypes = new ArrayList<>();

    public static void init() {
        register(new String[] {
           ""
        });
    }

    public static void register(String[] srg) {
        SupportedTypes.addAll(List.of(srg));
    }

    public static boolean isSupported(RecipeType<?> recipeType) {
        return isSupported(BuiltInRegistries.RECIPE_TYPE.getKey(recipeType));
    }

    public static boolean isSupported(@Nullable ResourceLocation recipeType) {
        if(recipeType == null) return true;
        return isSupported(recipeType.toString());
    }

    public static boolean isSupported(String recipeType) {
        return SupportedTypes.contains(recipeType);
    }
}
