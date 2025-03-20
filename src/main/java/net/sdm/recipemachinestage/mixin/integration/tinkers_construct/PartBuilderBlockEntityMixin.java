package net.sdm.recipemachinestage.mixin.integration.tinkers_construct;

import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.tables.block.entity.table.PartBuilderBlockEntity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(value = PartBuilderBlockEntity.class, remap = false)
public abstract class PartBuilderBlockEntityMixin {

    @Shadow @Nullable private Map<Pattern, IPartBuilderRecipe> recipes;
    @Unique
    private PartBuilderBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getCurrentRecipes", at = @At("RETURN"), cancellable = true)
    protected void sdm$getCurrentRecipes(CallbackInfoReturnable<Map<Pattern, IPartBuilderRecipe>> cir) {
        Map<Pattern, IPartBuilderRecipe> newRecipes = new HashMap<>();

        Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();

            for (Map.Entry<Pattern, IPartBuilderRecipe> entry : cir.getReturnValue().entrySet()) {
                IPartBuilderRecipe recipe = entry.getValue();

                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if(recipeBlockType == null) {
                    newRecipes.put(entry.getKey(), entry.getValue());
                    continue;
                }
                PlayerHelper.RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                if(player == null) {
                    newRecipes.put(entry.getKey(), entry.getValue());
                    continue;
                }

                if(player.hasStage(recipeBlockType.stage)) {
                    newRecipes.put(entry.getKey(), entry.getValue());
                }
            }
        }

        cir.setReturnValue(newRecipes);

    }

    @Inject(method = "getPartRecipe", at = @At("RETURN"), cancellable = true)
    public void sdm$getPartRecipe(CallbackInfoReturnable<IPartBuilderRecipe> cir) {
        IPartBuilderRecipe recipe = cir.getReturnValue();
        if(recipe == null) return;

        Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();

            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                if(player != null) {
                    if(!player.hasStage(recipeBlockType.stage)) {
                        cir.setReturnValue(null);
                    }
                }
            }
        }
    }
}
