package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.irons_spell_books;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import io.redspace.ironsspellbooks.block.alchemist_cauldron.AlchemistCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(AlchemistCauldronTile.class)
public abstract class AlchemistCauldronTileMixin extends BlockEntity {

    protected AlchemistCauldronTileMixin(BlockEntityType<?> arg, BlockPos arg2, BlockState arg3) {
        super(arg, arg2, arg3);
    }

    @Redirect(method = {"tryMeltInput", "tryExecuteRecipeInteractions"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> bts$tryMeltInput(RecipeManager instance, RecipeType<T> recipeType, I recipeInput, Level level) {
        final Optional<RecipeHolder<T>> getting = instance.getRecipeFor(recipeType, recipeInput, level);
        if(getting.isEmpty()) return getting;
        final RecipeHolder<T> recipe = getting.get();

        return RMSUtils.canProcess(this, recipe) ? getting : Optional.empty();
    }
}
