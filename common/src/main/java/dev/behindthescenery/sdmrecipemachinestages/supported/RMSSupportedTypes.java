package dev.behindthescenery.sdmrecipemachinestages.supported;

import dev.architectury.platform.Platform;
import dev.behindthescenery.sdmrecipemachinestages.exceptions.RecipeTypeNotSupported;
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
                "minecraft:smelting",
                "minecraft:stonecutting",
                "minecraft:crafting",
                "minecraft:smoking",
                "minecraft:blasting",
                "minecraft:smithing",
                "ae2:inscriber",
                "ae2:charger",
                "ae2:entropy",
                "ae2:transform",
                "apothic_enchanting:infusion",
                "apotheosis:gem_cutting",
                "apotheosis:salvaging",
                "apothic_spawners:spawner_modifier"
        });
    }

    public static void register(String[] srg) {
        SupportedTypes.addAll(List.of(srg));
    }

    public static void isSupported(RecipeType<?> recipeType) {
        isSupported(BuiltInRegistries.RECIPE_TYPE.getKey(recipeType));
    }

    public static void isSupported(@Nullable ResourceLocation recipeType) {
        if(recipeType == null) return;
        isSupported(recipeType.toString());
    }

    public static void isSupported(String recipeType) {
        if(Platform.isDevelopmentEnvironment() || SupportedTypes.contains(recipeType)) return;
        throw new RecipeTypeNotSupported(recipeType);
    }
}
