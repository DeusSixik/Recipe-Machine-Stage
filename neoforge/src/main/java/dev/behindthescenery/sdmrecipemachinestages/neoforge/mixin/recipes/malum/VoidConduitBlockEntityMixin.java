package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.malum;

import com.sammy.malum.common.block.curiosities.weeping_well.VoidConduitBlockEntity;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.utils.RMSLodeStoneUtils;
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
import team.lodestar.lodestone.systems.blockentity.LodestoneBlockEntity;


@Mixin(VoidConduitBlockEntity.class)
public abstract class VoidConduitBlockEntityMixin extends LodestoneBlockEntity {

    protected VoidConduitBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(method = "spitOutItem",
            at = @At(value = "INVOKE", target = "Lteam/lodestar/lodestone/systems/recipe/LodestoneRecipeType;getRecipe(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;)Lnet/minecraft/world/item/crafting/Recipe;"))
    public <T extends RecipeInput, K extends Recipe<T>> K sdm$tryRepair(Level level, RecipeType<K> recipeType, T recipeInput){
        return RMSLodeStoneUtils.getRecipeDistance(level, recipeType, recipeInput, this);
    }
}
