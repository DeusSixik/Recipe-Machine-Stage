package net.sdm.recipemachinestage.mixin.integration.mna;

import com.mna.api.blocks.tile.MultiblockTile;
import com.mna.blocks.tileentities.EldrinAltarTile;
import com.mna.recipes.eldrin.EldrinAltarRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EldrinAltarTile.class, remap = false)
public abstract class EldrinAltarTileMixin extends MultiblockTile {

    @Shadow private EldrinAltarRecipe __cachedRecipe;

    protected EldrinAltarTileMixin(BlockEntityType<?> type, ResourceLocation structure, BlockPos pos, BlockState state, int inventorySize) {
        super(type, structure, pos, state, inventorySize);
    }

    @Inject(method = "startCrafting", at = @At(value = "INVOKE", target = "Lcom/mna/blocks/tileentities/EldrinAltarTile;cacheRecipe(Lnet/minecraft/world/entity/player/Player;Z)Z"), cancellable = true)
    public void sdm$startCrafting(Player player, CallbackInfoReturnable<Boolean> cir) {
        if(__cachedRecipe == null) return;
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(__cachedRecipe.getType())) return;

        if (!RecipeStagesUtil.canRecipeOnBlockEntity(this, __cachedRecipe)) {
            cir.setReturnValue(false);
            setChanged();
        }
    }
}
