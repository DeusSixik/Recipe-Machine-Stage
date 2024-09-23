package net.sdm.recipemachinestage.mixin.integration.botania;


import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.api.recipe.OrechidRecipe;
import vazkii.botania.common.block.flower.functional.OrechidBlockEntity;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(value = OrechidBlockEntity.class, remap = false)
public abstract class OrechidBlockEntityMixin {
    @Shadow public abstract RecipeType<? extends OrechidRecipe> getRecipeType();

    @Unique
    public OrechidBlockEntity recipe_machine_stage$thisBlockEntity = (OrechidBlockEntity)(Object)this;


    @Inject(method = "findMatchingRecipe", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void sdm$findMatchingRecipe(BlockPos coords, CallbackInfoReturnable<OrechidRecipe> cir, BlockState input, List<WeightedEntry.Wrapper<OrechidRecipe>> values, Iterator var4, OrechidRecipe recipe){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(this.getRecipeType())) return;

        Optional<IOwnerBlock> d1 = recipe_machine_stage$thisBlockEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && recipe_machine_stage$thisBlockEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                ServerPlayer player = PlayerHelper.getPlayerByGameProfile(recipe_machine_stage$thisBlockEntity.getLevel().getServer(), ownerBlock.getOwner());
                if(player != null) {
                    if(!GameStageHelper.hasStage(player, recipeBlockType.stage)) {
                        cir.cancel();
                    }
                }
            }
        }
    }
}
