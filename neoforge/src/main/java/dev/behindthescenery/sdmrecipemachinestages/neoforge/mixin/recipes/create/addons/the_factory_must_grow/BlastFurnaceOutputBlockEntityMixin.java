package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create.addons.the_factory_must_grow;

import com.drmangotea.tfmg.content.machinery.metallurgy.blast_furnace.BlastFurnaceOutputBlockEntity;
import com.drmangotea.tfmg.registry.TFMGRecipeTypes;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(BlastFurnaceOutputBlockEntity.class)
public abstract class BlastFurnaceOutputBlockEntityMixin extends SmartBlockEntity {

    protected BlastFurnaceOutputBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/drmangotea/tfmg/registry/TFMGRecipeTypes;find(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> bts$tick(TFMGRecipeTypes instance, I inv, Level world) {
        final Optional<RecipeHolder<R>> recipeHolder = instance.find(inv, world);
        if(recipeHolder.isEmpty()) return recipeHolder;

        final RecipeHolder<R> getting = recipeHolder.get();
        return RMSUtils.canProcess(this, getting) ? recipeHolder : Optional.empty();
    }
}
