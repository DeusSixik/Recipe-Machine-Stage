package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.malum;

import com.sammy.malum.common.block.curiosities.runic_workbench.RunicWorkbenchBlockEntity;
import com.sammy.malum.common.block.storage.MalumItemHolderBlockEntity;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.utils.RMSLodeStoneUtils;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RunicWorkbenchBlockEntity.class)
public abstract class RunicWorkbenchBlockEntityMixin extends MalumItemHolderBlockEntity {

    protected RunicWorkbenchBlockEntityMixin(BlockEntityType<? extends MalumItemHolderBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(method = "tryCraft",
            at = @At(value = "INVOKE", target = "Lteam/lodestar/lodestone/systems/recipe/LodestoneRecipeType;getRecipe(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;)Lnet/minecraft/world/item/crafting/Recipe;"))
    public <T extends RecipeInput, K extends Recipe<T>> K sdm$tryCraft(Level level, RecipeType<K> recipeType, T recipeInput){
        return RMSLodeStoneUtils.getRecipe(level, recipeType, recipeInput, RMSUtils.getBlockOwner(this));
    }
}
