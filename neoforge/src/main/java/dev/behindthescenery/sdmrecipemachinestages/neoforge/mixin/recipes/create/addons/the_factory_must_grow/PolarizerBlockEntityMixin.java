package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create.addons.the_factory_must_grow;

import com.drmangotea.tfmg.content.electricity.base.ElectricBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.polarizer.PolarizerBlockEntity;
import com.drmangotea.tfmg.recipes.PolarizingRecipe;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = PolarizerBlockEntity.class)
public abstract class PolarizerBlockEntityMixin extends ElectricBlockEntity {


    protected PolarizerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "getRecipe", at = @At("RETURN"), cancellable = true)
    public void sdm$getRecipe(ItemStack item, CallbackInfoReturnable<Optional<RecipeHolder<PolarizingRecipe>>> cir) {
        final Optional<RecipeHolder<PolarizingRecipe>> getter = cir.getReturnValue();
        if(getter.isEmpty()) return;

        cir.setReturnValue(Optional.ofNullable(RMSUtils.ifCanProcessReturnRecipeOrNull(this, getter.get())));
    }
}
