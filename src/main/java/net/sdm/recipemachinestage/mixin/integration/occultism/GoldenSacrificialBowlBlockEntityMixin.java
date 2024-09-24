package net.sdm.recipemachinestage.mixin.integration.occultism;

import com.klikli_dev.occultism.common.blockentity.GoldenSacrificialBowlBlockEntity;
import com.klikli_dev.occultism.crafting.recipe.RitualRecipe;
import com.klikli_dev.occultism.registry.OccultismRecipes;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.sdm.recipemachinestage.RecipeMachineStage;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(value = GoldenSacrificialBowlBlockEntity.class, remap = false)
public class GoldenSacrificialBowlBlockEntityMixin {

    private GoldenSacrificialBowlBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Redirect(method = "activate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getAllRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;)Ljava/util/List;"))
    public List<?> sdm$activate(RecipeManager instance, RecipeType p_44014_){
        try {
            List<?> mutableRecipeList = instance.getAllRecipesFor(p_44014_);
            List<?> recipeOp = new ArrayList<>(mutableRecipeList);

            if (StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(OccultismRecipes.RITUAL_TYPE.get())) {
                return recipeOp;
            }

            Iterator iterator = recipeOp.iterator();

            while (iterator.hasNext()) {
                Recipe recipe = (Recipe) iterator.next();
                Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
                if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
                    IOwnerBlock ownerBlock = d1.get();
                    RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                    if (recipeBlockType != null) {
                        PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                        if (player != null) {
                            if (!player.hasStage(recipeBlockType.stage)) {
                                iterator.remove();
                            }
                        }
                    }
                }
            }


            return recipeOp;
        } catch (Exception e) {
            RecipeMachineStage.LOGGER.error(e.getMessage());
        }
        return new ArrayList<>();
    }
}
