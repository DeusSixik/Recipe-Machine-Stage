package net.sdm.recipemachinestage.mixin.integration.bloodmagic;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wayoftime.bloodmagic.common.tile.TileSoulForge;
import wayoftime.bloodmagic.impl.BloodMagicRecipeRegistrar;
import wayoftime.bloodmagic.recipe.RecipeTartaricForge;

import java.util.List;
import java.util.Optional;

@Mixin(value = TileSoulForge.class, remap = false)
public class TileSoulForgeMixin {

     private TileSoulForge thisEntity = RecipeStagesUtil.cast(this);

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lwayoftime/bloodmagic/impl/BloodMagicRecipeRegistrar;getTartaricForge(Lnet/minecraft/world/level/Level;Ljava/util/List;)Lwayoftime/bloodmagic/recipe/RecipeTartaricForge;"))
    private RecipeTartaricForge sdm$tick$getTartaricForge(BloodMagicRecipeRegistrar instance, Level j, List<ItemStack> matched) {
        RecipeTartaricForge recipe = instance.getTartaricForge(j, matched);

        if(recipe != null && StageContainer.hasRecipes(recipe.getType())) {
            Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer()!= null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if(recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!player.hasStage(recipeBlockType.stage)) {
                            return null;
                        }
                    }
                }
            }
        }

        return recipe;
    }
}
