package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.woot_revived;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wootrevived.woot.blocks.stygian_anvil.StygianAnvilBlockEntity;

import java.util.Optional;

@Mixin(StygianAnvilBlockEntity.class)
public abstract class StygianAnvilBlockEntityMixin extends BlockEntity {
    protected StygianAnvilBlockEntityMixin(BlockEntityType<?> arg, BlockPos arg2, BlockState arg3) {
        super(arg, arg2, arg3);
    }

    @Redirect(method = "tryCraft", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> bts$getRecipe(RecipeManager instance, RecipeType<T> recipeType, I recipeInput, Level level) {
        final Optional<RecipeHolder<T>> find = instance.getRecipeFor(recipeType, recipeInput, level);
        if(find.isEmpty()) return find;

        final RecipeHolder<T> getting = find.get();
        return RMSUtils.canProcess(this, getting) ? find : Optional.empty();
    }
}
