package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create.addons.the_factory_must_grow;

import com.drmangotea.tfmg.content.machinery.misc.winding_machine.WindingMachineBlockEntity;
import com.drmangotea.tfmg.recipes.WindingRecipe;
import com.drmangotea.tfmg.registry.TFMGRecipeTypes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.item.SmartInventory;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(WindingMachineBlockEntity.class)
public class WindingMachineBlockEntityMixin extends KineticBlockEntity {

    @Shadow
    public SmartInventory inventory;

    @Shadow
    public WindingRecipe recipe;

    protected WindingMachineBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    /**
     * @author Sixik
     * @reason Change Logic
     */
    @Overwrite
    public void findRecipe() {
        final Optional<RecipeHolder<WindingRecipe>> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(this.level, new RecipeWrapper(this.inventory), TFMGRecipeTypes.WINDING.getType(), WindingRecipe.class);
        if(assemblyRecipe.isPresent()) {
            final RecipeHolder<WindingRecipe> recipe = assemblyRecipe.get();
            this.recipe = RMSUtils.canProcess(this, recipe) ? recipe.value() : null;
            return;
        }

        final Optional<RecipeHolder<WindingRecipe>> optional = TFMGRecipeTypes.WINDING.find(new RecipeWrapper(this.inventory), this.level);
        if(optional.isPresent()) {
            final RecipeHolder<WindingRecipe> recipe = optional.get();
            final WindingRecipe rd = RMSUtils.canProcess(this, recipe) ? recipe.value() : null;
            if(rd == null) return;
            if (rd.getIngredient().test(this.inventory.getItem(0))) {
                this.recipe = rd;
            }

            return;
        }

        this.recipe = null;
    }
}
