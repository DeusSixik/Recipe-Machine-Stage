package net.sdm.recipemachinestage.mixin.integration.botania;


import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.recipe.BotanicalBreweryRecipe;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.BreweryBlockEntity;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;

import java.util.Optional;

@Mixin(value = BreweryBlockEntity.class, remap = false)
public class BreweryBlockEntityMixin {

    @Unique
    public BreweryBlockEntity recipe_machine_stage$thisEntity = (BreweryBlockEntity)(Object)this;
    @Shadow public BotanicalBreweryRecipe recipe;

    @Inject(method = "findRecipe", at = @At("HEAD"), cancellable = true)
    public void sdm$findRecipe(CallbackInfo ci){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(BotaniaRecipeTypes.BREW_TYPE)) return;
        ci.cancel();

        Optional<BotanicalBreweryRecipe> maybeRecipe = recipe_machine_stage$thisEntity.getLevel().getRecipeManager().getRecipeFor(BotaniaRecipeTypes.BREW_TYPE, this.recipe_machine_stage$thisEntity.getItemHandler(), recipe_machine_stage$thisEntity.getLevel());

        maybeRecipe.ifPresent((recipeBrew) -> {
            Optional<IOwnerBlock> d1 = recipe_machine_stage$thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
            if (d1.isPresent() && recipe_machine_stage$thisEntity.getLevel().getServer() != null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipeBrew.getType(), recipeBrew.getId());
                if(recipeBlockType != null) {
                    ServerPlayer player = PlayerHelper.getPlayerByGameProfile(recipe_machine_stage$thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!GameStageHelper.hasStage(player, recipeBlockType.stage)) {
                           return;
                        }
                    }
                }
            }

            this.recipe = recipeBrew;
            recipe_machine_stage$thisEntity.getLevel().setBlockAndUpdate(recipe_machine_stage$thisEntity.getBlockPos(), (BlockState) BotaniaBlocks.brewery.defaultBlockState().setValue(BlockStateProperties.POWERED, true));
        });
    }
}
