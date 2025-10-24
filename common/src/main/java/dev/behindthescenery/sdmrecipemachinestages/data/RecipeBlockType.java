package dev.behindthescenery.sdmrecipemachinestages.data;

import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmstages.data.containers.Stage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public record RecipeBlockType(String stage_id, RecipeType<?> recipeType, List<ResourceLocation> recipes_id) implements BaseRecipeStage {

    public boolean hasStage(ServerPlayer serverPlayer) {
        final Stage stage = RMSMain.getStageContainer().getStage(serverPlayer);
        if(stage == null) return false;
        return stage.contains(stage_id);
    }

    public boolean contains(ResourceLocation recipe_id) {
        return recipes_id.contains(recipe_id);
    }
}
