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

    private static final List<String> SupportedByTypes = new ArrayList<>();
    private static final List<String> SupportedByMods = new ArrayList<>();

    public static void init() {
        registerMods(new String[] {
                "mekanism"
        });

        registerType(new String[] {
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
                "apothic_spawners:spawner_modifier",
                "create:mixing",
                "create:compacting",
                "create:filling",
                "create:basin",
                "create:crushing",
                "create:cutting",
                "create:splashing",
                "create:deploying",
                "create:milling",
                "create:mechanical_crafting",
                "create:sequenced_assembly",
                "create:haunting",
                "create:sandpaper_polishing",
                "create:pressing",
                "tfmg:winding",
                "tfmg:casting",
                "tfmg:distillation",
                "tfmg:polarizing",
                "tfmg:vat_machine_recipe",
                "tfmg:coking",
                "tfmg:hot_blast",
                "tfmg:industrial_blasting",
                "irons_spellbooks:alchemist_cauldron_fill",
                "irons_spellbooks:alchemist_cauldron_empty",
                "irons_spellbooks:alchemist_cauldron_brew",
                "jumbofurnace:jumbo_smelting",
                "malum:favor_of_the_void",
                "malum:soul_binding",
                "malum:spirit_infusion",
                "malum:spirit_repair",
                "malum:runeworking",
                "malum:spirit_focusing"
        });
    }

    public static void registerType(String[] srg) {
        SupportedByTypes.addAll(List.of(srg));
    }

    public static void registerMods(String[] srg) {
        SupportedByMods.addAll(List.of(srg));
    }

    public static void isSupported(RecipeType<?> recipeType) {
        isSupported(BuiltInRegistries.RECIPE_TYPE.getKey(recipeType));
    }

    public static void isSupported(@Nullable ResourceLocation recipeType) {
        if(recipeType == null) return;
        isSupported(recipeType.toString(), recipeType.getNamespace());
    }

    public static void isSupported(String full, String namespace) {
        if(Platform.isDevelopmentEnvironment() || SupportedByMods.contains(namespace) || SupportedByTypes.contains(full)) return;
        throw new RecipeTypeNotSupported(full);
    }
}
