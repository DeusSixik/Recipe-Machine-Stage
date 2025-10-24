package net.sdm.recipemachinestage.mixin.integration.railcraft;

import mods.railcraft.world.item.crafting.RailcraftRecipeTypes;
import mods.railcraft.world.module.BlastFurnaceModule;
import mods.railcraft.world.module.CokeOvenModule;
import mods.railcraft.world.module.CookingModule;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = CookingModule.class, remap = false)
public class CookingModuleMixin<R extends AbstractCookingRecipe> {

    private CookingModule module = RecipeStagesUtil.cast(this);

    @Inject(method = "getRecipeFor", at = @At("RETURN"))
    public void sdm$getRecipe(CallbackInfoReturnable<Optional<R>> cir){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty()) return;

        BlockEntity thisEntity = null;

        if(module.getProvider() instanceof CokeOvenModule cokeOvenModule) {
            if(!StageContainer.INSTANCE.RECIPES_STAGES.containsKey(RailcraftRecipeTypes.COKING.get())) return;
            thisEntity = cokeOvenModule.getProvider();
        } else if(module.getProvider() instanceof BlastFurnaceModule cokeOvenModule) {
            if(!StageContainer.INSTANCE.RECIPES_STAGES.containsKey(RailcraftRecipeTypes.BLASTING.get())) return;
            thisEntity = cokeOvenModule.getProvider();
        }

        if(thisEntity == null) return;

        Optional<R> recipeOptional = cir.getReturnValue();
        if(recipeOptional.isPresent()) {
            var recipe = recipeOptional.get();
            Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if(recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!player.hasStage(recipeBlockType.stage)) {
                            cir.cancel();
                        }
                    }
                }
            }
        }
    }
}
