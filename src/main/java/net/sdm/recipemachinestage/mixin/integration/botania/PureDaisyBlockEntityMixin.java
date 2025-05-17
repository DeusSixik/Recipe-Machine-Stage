package net.sdm.recipemachinestage.mixin.integration.botania;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.api.recipe.PureDaisyRecipe;
import vazkii.botania.common.block.flower.PureDaisyBlockEntity;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;

import java.util.Optional;

@Mixin(value = PureDaisyBlockEntity.class, remap = false)
public abstract class PureDaisyBlockEntityMixin {

    @Shadow @Nullable protected abstract PureDaisyRecipe findRecipe(BlockPos coords);

    @Unique
    private PureDaisyBlockEntity recipe_machine_stage$thisDaisy = (PureDaisyBlockEntity)(Object) this;

    @Inject(method = "tickFlower", at = @At(value = "INVOKE", target = "Lvazkii/botania/common/block/flower/PureDaisyBlockEntity;findRecipe(Lnet/minecraft/core/BlockPos;)Lvazkii/botania/api/recipe/PureDaisyRecipe;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void sdm$tickFlower(CallbackInfo ci, BlockPos acoords, BlockPos coords, Level world){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(BotaniaRecipeTypes.PURE_DAISY_TYPE)) return;

        PureDaisyRecipe recipe = this.findRecipe(coords);
        Optional<IOwnerBlock> d1 = recipe_machine_stage$thisDaisy.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        if (d1.isPresent() && world.getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(world.getServer(), ownerBlock.getOwner());
                if(player != null) {
                    if(!player.hasStage(recipeBlockType.stage)) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}
