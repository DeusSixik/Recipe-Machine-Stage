package dev.behindthescenery.sdmrecipemachinestages.utils;

import net.minecraft.resources.ResourceLocation;

public class RMSTextConverter {

    public static ResourceLocation tryConvert(ResourceLocation recipeType) {
        return mod_create(recipeType);
    }

    public static ResourceLocation mod_create(ResourceLocation recipeType) {
        if(!recipeType.getNamespace().equals("create")) return recipeType;

        final String path = recipeType.getPath();
        return switch (path) {
            case "splashing" -> create_newId("fan_washing");
            case "smoking" -> create_newId("fan_smoking");
            case "smelting" -> create_newId("fan_blasting");
            case "haunting" -> create_newId("fan_haunting");
            case "compacting" -> create_newId("packing");
            case "cutting" -> create_newId("sawing");
            case "filling" -> create_newId("spout_filling");
            case "emptying" -> create_newId("draining");
            default -> recipeType;
        };
    }

    private static ResourceLocation create_newId(String path) {
        return ResourceLocation.tryBuild("create", path);
    }
}
