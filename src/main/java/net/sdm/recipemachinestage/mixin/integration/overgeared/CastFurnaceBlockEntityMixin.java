package net.sdm.recipemachinestage.mixin.integration.overgeared;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sdm.recipemachinestage.compat.overgeared.OvergearedRecipeSupport;
import net.sdm.recipemachinestage.utils.RMSUtils;
import net.stirdrem.overgeared.block.entity.CastFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

/**
 * Applies recipe-stage restrictions to the Overgeared casting furnace.
 */
@Mixin(CastFurnaceBlockEntity.class)
public abstract class CastFurnaceBlockEntityMixin {

    /**
     * Filters casting lookups used for validation and processing.
     *
     * @param recipeManager the active recipe manager.
     * @param recipeType the casting recipe type being queried.
     * @param container the material-and-cast furnace input.
     * @param level the level used for recipe matching.
     * @param <C> the recipe container type.
     * @param <T> the recipe value type.
     * @return the matching recipe when the furnace owner has access, otherwise an empty optional.
     */
    @Redirect(
            method = {"canSmelt", "smelt"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
            )
    )
    private <C extends Container, T extends Recipe<C>> Optional<T> recipeMachineStage$filterCastingRecipe(
            RecipeManager recipeManager,
            RecipeType<T> recipeType,
            C container,
            Level level
    ) {
        final BlockEntity blockEntity = (BlockEntity) (Object) this;
        return OvergearedRecipeSupport.filter(
                recipeManager.getRecipeFor(recipeType, container, level),
                recipe -> RMSUtils.canRecipe(recipe, blockEntity)
        );
    }
}
