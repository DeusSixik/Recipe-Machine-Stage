package net.sdm.recipemachinestage.mixin.integration.ars_nouveau;

import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import net.minecraft.world.entity.player.Player;
import net.sdm.recipemachinestage.RecipeMachineStage;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RMSUtils;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ScribesTile.class, remap = false)
public class ScribesTileMixin {

    private ScribesTile thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "setRecipe", at = @At("HEAD"), cancellable = true)
    public void sdm$setRecipe(GlyphRecipe recipe, Player player, CallbackInfo ci){

        if(!RMSUtils.canRecipe(recipe, player))
            ci.cancel();
    }
}
