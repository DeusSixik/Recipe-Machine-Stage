package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.natures_aura;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityWoodStand;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityWoodStand.class)
public abstract class BlockEntityWoodStandMixin extends BlockEntityImpl {

    @Shadow
    private RecipeHolder<TreeRitualRecipe> recipe;

    public BlockEntityWoodStandMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "isRitualOkay", at = @At("HEAD"), cancellable = true)
    public void bts$isRitualOkay(CallbackInfoReturnable<Boolean> cir) {
        if(!RMSUtils.canProcess(RMSUtils.getBlockOwner(this), recipe))
            cir.setReturnValue(false);
    }
}
