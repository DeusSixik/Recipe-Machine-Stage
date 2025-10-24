package net.sdm.recipemachinestage.mixin.integration.tinkers_construct.modules;

import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.smeltery.block.entity.module.alloying.MultiAlloyingModule;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(value = MultiAlloyingModule.class, remap = false)
public class MultiAlloyingModuleMixin {

    @Shadow @Final private MantleBlockEntity parent;

    @Inject(method = "getRecipes", at = @At("RETURN"))
    private void sdm$getRecipes(CallbackInfoReturnable<List<AlloyRecipe>> cir) {
        Iterator<AlloyRecipe> list = cir.getReturnValue().iterator();
        while (list.hasNext()) {
            AlloyRecipe recipe = list.next();

            Optional<IOwnerBlock> d1 = parent.getCapability(RMSCapability.BLOCK_OWNER).resolve();
            if (d1.isPresent() && parent.getLevel().getServer() != null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if(recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(parent.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!player.hasStage(recipeBlockType.stage)) {
                            list.remove();
                        }
                    }
                }
            }
        }
    }
}
