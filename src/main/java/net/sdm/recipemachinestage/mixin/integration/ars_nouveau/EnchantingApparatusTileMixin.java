package net.sdm.recipemachinestage.mixin.integration.ars_nouveau;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnchantingApparatusTile.class, remap = false)
public class EnchantingApparatusTileMixin {

    private EnchantingApparatusTile thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getRecipe", at = @At("RETURN"), cancellable = true)
    public void sdm$getRecipe(ItemStack stack, Player playerEntity, CallbackInfoReturnable<IEnchantingRecipe> cir){
        IEnchantingRecipe recipe = cir.getReturnValue();

        if(recipe == null) return;

        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(recipe.getType())) return;

        if (thisEntity.getLevel().getServer() != null) {
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData _player = PlayerHelper.getPlayerByGameProfile(playerEntity.getServer(), playerEntity.getGameProfile().getId());
                if(_player != null) {
                    if(!_player.hasStage(recipeBlockType.stage))
                        cir.setReturnValue(null);
                }
            }
        }
    }
}
