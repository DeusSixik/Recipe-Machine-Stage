package dev.behindthescenery.sdmrecipemachinestages.mixin.recipes.ice_and_fire;

import com.iafenvoy.iceandfire.item.block.entity.DragonForgeBlockEntity;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
import com.iafenvoy.iceandfire.registry.IafRecipes;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(DragonForgeBlockEntity.class)
public abstract class DragonForgeBlockEntityMixin extends BaseContainerBlockEntity {

    protected DragonForgeBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }


    /**
     * @author Sixik
     * @reason Change logic
     */
    @Overwrite
    public Optional<DragonForgeRecipe> getCurrentRecipe() {
        assert this.level != null;

        final Optional<RecipeHolder<DragonForgeRecipe>> find = this.level.getRecipeManager().getRecipeFor(IafRecipes.DRAGON_FORGE_TYPE.get(), new DragonForgeBlockEntity.DragonForgeRecipeInput((DragonForgeBlockEntity) (Object) this), this.level);
        if(find.isEmpty()) return Optional.empty();
        final RecipeHolder<DragonForgeRecipe> getting = find.get();
        return RMSUtils.canProcess(this, getting) ? Optional.of(getting.value()) : Optional.empty();
    }

    /**
     * @author Sixik
     * @reason Change logic
     */
    @Overwrite
    public List<DragonForgeRecipe> getRecipes() {
        assert this.level != null;
        return RMSUtils.filterRecipes(this.level.getRecipeManager().getAllRecipesFor(IafRecipes.DRAGON_FORGE_TYPE.get()), RMSUtils.getBlockOwner(this)).stream().map(RecipeHolder::value).toList();
    }
}
