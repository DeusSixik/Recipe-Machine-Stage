package net.sdm.recipemachinestage.mixin.integration.eidolon;

import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.common.tile.BrazierTileEntity;
import elucent.eidolon.recipe.RitualRecipe;
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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(value = BrazierTileEntity.class, remap = false)
public abstract class BrazierTileEntityMixin {

    @Shadow protected abstract void setRitual(Ritual ritual);

    @Shadow private int findingCounter;
    @Shadow private boolean ritualDone;

    @Shadow protected abstract void extinguish();

    private BrazierTileEntity thisEntity = RecipeStagesUtil.cast(this);


    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lelucent/eidolon/common/tile/BrazierTileEntity;getRitualRecipes(Lnet/minecraft/world/level/Level;)Ljava/util/List;"), cancellable = true)
    public void sdm$setRitual(CallbackInfo ci){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty()) return;
        ci.cancel();
        List<RitualRecipe> recipes = BrazierTileEntity.getRitualRecipes(thisEntity.getLevel());
        Iterator<RitualRecipe> iterator = recipes.iterator();

        Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
            if(player != null) {
                while (iterator.hasNext()) {
                    RitualRecipe recipe = iterator.next();
                    RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                    if (recipeBlockType != null) {
                        if (!player.hasStage(recipeBlockType.stage)) {
                            iterator.remove();
                        }
                    }
                }
            }
        }

        recipes.stream().filter((recipe) -> {
            return recipe.matches(thisEntity, thisEntity.getLevel());
        }).findFirst().ifPresentOrElse((recipe) -> {
            this.setRitual(recipe.getRitualWithRequirements());
            thisEntity.setStack(ItemStack.EMPTY);
            this.findingCounter = 81;
        }, () -> {
            this.ritualDone = true;
            this.extinguish();
        });
    }
}
