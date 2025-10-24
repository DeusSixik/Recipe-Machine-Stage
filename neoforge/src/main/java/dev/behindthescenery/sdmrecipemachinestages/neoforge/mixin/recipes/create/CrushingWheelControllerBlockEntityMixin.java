package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create;

import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CrushingWheelControllerBlockEntity.class)
public abstract class CrushingWheelControllerBlockEntityMixin extends SmartBlockEntity {

    protected CrushingWheelControllerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "findRecipe", at = @At("RETURN"), cancellable = true)
    public void bts$findRecipe(CallbackInfoReturnable<Optional<RecipeHolder<StandardProcessingRecipe<RecipeWrapper>>>> cir) {
        final Optional<RecipeHolder<StandardProcessingRecipe<RecipeWrapper>>> returnRecipe = cir.getReturnValue();

        if(returnRecipe.isEmpty()) return;

        final RecipeHolder<StandardProcessingRecipe<RecipeWrapper>> recipe = returnRecipe.get();
        cir.setReturnValue(RMSUtils.canProcess(RMSUtils.getBlockOwner(this), recipe) ? returnRecipe : Optional.empty());
    }
}
