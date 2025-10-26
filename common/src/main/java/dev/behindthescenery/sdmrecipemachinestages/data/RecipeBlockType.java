package dev.behindthescenery.sdmrecipemachinestages.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RecipeBlockType(String stageId, RecipeType<?> recipeType, List<ResourceLocation> recipes_id) implements BaseRecipeStage {

    public boolean contains(ResourceLocation recipe_id) {
        return recipes_id.contains(recipe_id);
    }

    @Override
    public @NotNull String toString() {
        return "RecipeBlockType{" +
                "stageId='" + stageId + '\'' +
                ", recipeType=" + recipeType +
                ", recipes_id=" + recipes_id +
                '}';
    }
}
