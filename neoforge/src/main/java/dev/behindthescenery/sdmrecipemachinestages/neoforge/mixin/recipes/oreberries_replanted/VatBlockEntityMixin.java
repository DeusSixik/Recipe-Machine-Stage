package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.oreberries_replanted;

import com.mrbysco.oreberriesreplanted.blockentity.VatBlockEntity;
import com.mrbysco.oreberriesreplanted.recipes.VatRecipe;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VatBlockEntity.class)
public class VatBlockEntityMixin extends BlockEntity {

    protected VatBlockEntityMixin(BlockEntityType<?> arg, BlockPos arg2, BlockState arg3) {
        super(arg, arg2, arg3);
    }

    @Inject(method = "getRecipe", at = @At("RETURN"), cancellable = true)
    public void bts$getRecipe(CallbackInfoReturnable<RecipeHolder<VatRecipe>> cir) {
        final RecipeHolder<VatRecipe> getting = cir.getReturnValue();
        if(getting == null) return;

        if(!RMSUtils.canProcess(this, getting))
            cir.setReturnValue(null);
    }
}
