package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.natures_aura;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAnimalSpawner;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(BlockEntityAnimalSpawner.class)
public class BlockEntityAnimalSpawnerMixin extends BlockEntityImpl {

    protected BlockEntityAnimalSpawnerMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/List;"))
    public <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> bts$tick$redirect(RecipeManager instance, RecipeType<T> recipeType, I recipeInput, Level level) {
        return RMSUtils.filterRecipes(instance.getRecipesFor(recipeType, recipeInput, level), RMSUtils.getBlockOwner(this));
    }
}
