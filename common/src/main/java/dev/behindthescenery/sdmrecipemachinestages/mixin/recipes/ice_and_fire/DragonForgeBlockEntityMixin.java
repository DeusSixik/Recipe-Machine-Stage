package dev.behindthescenery.sdmrecipemachinestages.mixin.recipes.ice_and_fire;

import com.iafenvoy.iceandfire.item.block.entity.DragonForgeBlockEntity;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
import com.iafenvoy.iceandfire.registry.IafRecipes;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(value = DragonForgeBlockEntity.class, remap = false)
public abstract class DragonForgeBlockEntityMixin extends BaseContainerBlockEntity {

    protected DragonForgeBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }



    @Inject(method = "getCurrentRecipe", at = @At("HEAD"), cancellable = true)
    public void getCurrentRecipe(CallbackInfoReturnable<Optional<DragonForgeRecipe>> cir) {
        assert this.level != null;

        final Optional<RecipeHolder<DragonForgeRecipe>> find = this.level.getRecipeManager().getRecipeFor(IafRecipes.DRAGON_FORGE_TYPE.get(), new DragonForgeBlockEntity.DragonForgeRecipeInput((DragonForgeBlockEntity) (Object) this), this.level);
        if(find.isEmpty()) {
            cir.setReturnValue(Optional.empty());
            return;
        }
        final RecipeHolder<DragonForgeRecipe> getting = find.get();
        cir.setReturnValue(RMSUtils.canProcess(this, getting) ? Optional.of(getting.value()) : Optional.empty());
    }

    @Inject(method = "getRecipes", at = @At("HEAD"), cancellable = true)
    public void getRecipes(CallbackInfoReturnable<List<DragonForgeRecipe>> cir) {
        assert this.level != null;
        cir.setReturnValue(RMSUtils.filterRecipes(this.level.getRecipeManager().getAllRecipesFor(IafRecipes.DRAGON_FORGE_TYPE.get()), RMSUtils.getBlockOwner(this)).stream().map(RecipeHolder::value).toList());
    }
}
