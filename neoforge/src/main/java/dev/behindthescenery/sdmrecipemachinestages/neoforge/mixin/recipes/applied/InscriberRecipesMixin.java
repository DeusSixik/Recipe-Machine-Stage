package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.applied;

import appeng.blockentity.misc.InscriberRecipes;
import appeng.core.definitions.AEItems;
import appeng.recipes.AERecipeTypes;
import appeng.recipes.handlers.InscriberRecipe;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(InscriberRecipes.class)
public abstract class InscriberRecipesMixin {

    @Shadow
    private static InscriberRecipe makeNamePressRecipe(ItemStack input, ItemStack plateA, ItemStack plateB) {
        throw new NotImplementedException();
    }

    /**
     * @author Sixik
     * @reason Add stage condition
     */
    @Overwrite
    public static @Nullable InscriberRecipe findRecipe(Level level, ItemStack input, ItemStack plateA, ItemStack plateB, boolean supportNamePress) {
        if (supportNamePress) {
            final boolean isNameA = AEItems.NAME_PRESS.is(plateA);
            final boolean isNameB = AEItems.NAME_PRESS.is(plateB);
            if (isNameA && isNameB || isNameA && plateB.isEmpty()) {
                return makeNamePressRecipe(input, plateA, plateB);
            }

            if (plateA.isEmpty() && isNameB) {
                return makeNamePressRecipe(input, plateB, plateA);
            }
        }

        final UUID blockOwner = RMSMain.getBlockOwner();
        for(final RecipeHolder<InscriberRecipe> holder : RMSUtils.filterRecipes(level.getRecipeManager().getAllRecipesFor(AERecipeTypes.INSCRIBER), blockOwner)) {
            final InscriberRecipe recipe = holder.value();
            final boolean matchA = recipe.getTopOptional().test(plateA) && recipe.getBottomOptional().test(plateB);
            final boolean matchB = recipe.getTopOptional().test(plateB) && recipe.getBottomOptional().test(plateA);
            if ((matchA || matchB) && recipe.getMiddleInput().test(input)) {
                return recipe;
            }
        }

        return null;
    }
}
