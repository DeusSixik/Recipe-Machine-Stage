package dev.behindthescenery.sdmrecipemachinestages.utils;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Function;

public class RMSTextConverter {

    private static Map<String, Function<ResourceLocation, ResourceLocation[]>> CONVERTERS = Map.of(
            "create", RMSTextConverter::mod_create,
            "modern_industrialization", RMSTextConverter::modernIndustrialization
    );

    public static ResourceLocation[] tryConvert(ResourceLocation recipeType) {
        return CONVERTERS.getOrDefault(recipeType.getNamespace(), data -> new ResourceLocation[] { data }).apply(recipeType);
    }

    public static ResourceLocation[] mod_create(ResourceLocation recipeType) {
        if(!recipeType.getNamespace().equals("create")) return new ResourceLocation[] { recipeType };

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
            default -> new ResourceLocation[] { recipeType };
        };
    }

    private static ResourceLocation[] create_newId(String path) {
        return new ResourceLocation[] { ResourceLocation.tryBuild("create", path) };
    }

    public static ResourceLocation[] modernIndustrialization(ResourceLocation recipeType) {
        if(!recipeType.getNamespace().equals("modern_industrialization")) return new ResourceLocation[] { recipeType };

        final String path = recipeType.getPath();
        return switch (path) {
            case "macerator" -> modernIndustrializationNewId("macerator");
            case "cutting_machine" -> modernIndustrializationNewId("cutting_machine");
            case "mixer" -> modernIndustrializationNewId("mixer");
            case "compressor" -> modernIndustrializationNewId("compressor");
            case "furnace" -> modernIndustrializationNewId("furnace");
            case "packer" -> modernIndustrializationNewId("packer");
            case "wiremill" -> modernIndustrializationNewId("wiremill");
            case "blast_furnace" -> modernIndustrializationNewBlastFurnace();
            default -> new ResourceLocation[] { recipeType };
        };
    }

    private static ResourceLocation[] modernIndustrializationNewId(String name) {
        return new ResourceLocation[] {
                ResourceLocation.tryBuild("modern_industrialization", "steel_" + name),
                ResourceLocation.tryBuild("modern_industrialization", "electric_" + name),
                ResourceLocation.tryBuild("modern_industrialization", "bronze_" + name)
        };
    }

    private static ResourceLocation[] modernIndustrializationNewBlastFurnace() {
        return new ResourceLocation[] {
                ResourceLocation.tryBuild("modern_industrialization", "electric_blast_furnace_cupronickel_coil"),
                ResourceLocation.tryBuild("modern_industrialization", "electric_blast_furnace_kanthal_coil"),
                ResourceLocation.tryBuild("modern_industrialization", "steam_blast_furnace"),
        };
    }
}
