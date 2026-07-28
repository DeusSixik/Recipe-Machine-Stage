package net.sdm.recipemachinestage.api.restriction_collector;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.sdm.recipemachinestage.api.RecipeRegisterType;
import org.jetbrains.annotations.Nullable;

public record RecipeRestrictionRawData(
        RecipeRegisterType registerType,
        Object stage,
        @Nullable BlockEntityType<?> block,
        @Nullable RecipeType<?> recipeType,
        ResourceLocation[] recipes
) { }
