package net.sdm.recipemachinestage.mixin.integration.thermal.manager;

import cofh.lib.util.crafting.ComparableItemStack;
import cofh.thermal.lib.util.managers.AbstractManager;
import cofh.thermal.lib.util.managers.SingleItemFuelManager;
import cofh.thermal.lib.util.recipes.ThermalFuel;
import cofh.thermal.lib.util.recipes.internal.BaseDynamoFuel;
import cofh.thermal.lib.util.recipes.internal.IDynamoFuel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.sdm.recipemachinestage.compat.thermal.IThermalRecipeAddition;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mixin(value = SingleItemFuelManager.class, remap = false)
public class SingleItemFuelManagerMixin {

    @Shadow
    protected Map<ComparableItemStack, IDynamoFuel> fuelMap;
    @Unique
    private ThermalFuel recipe_machine_stage$recipe;

    @Unique
    private SingleItemFuelManager recipe_machine_stage$thisRecipesManager = RecipeStagesUtil.cast(this);

    @Inject(method = "addFuel(Lcofh/thermal/lib/util/recipes/ThermalFuel;)V", at = @At(value = "INVOKE", target = "Lcofh/thermal/lib/util/managers/SingleItemFuelManager;addFuel(ILjava/util/List;Ljava/util/List;)Lcofh/thermal/lib/util/recipes/internal/IDynamoFuel;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void sdm$addRecipe$5(ThermalFuel recipe, CallbackInfo ci, ItemStack[] var2, int var3, int var4, ItemStack recipeInput) {
        ci.cancel();
        this.recipe_machine_stage$recipe = recipe;
        sdm$CustomAddFuel(recipe.getEnergy(), Collections.singletonList(recipeInput), Collections.emptyList());
    }

    @Unique
    public void sdm$CustomAddFuel(int energy, List<ItemStack> inputItems, List<FluidStack> inputFluids) {
        if (!inputItems.isEmpty() && energy > 0) {
            if (energy >= 1000 && energy <= 20000000) {
                ItemStack input = inputItems.get(0);
                if (input.isEmpty()) {
                } else {
                    energy = (int) ((float) energy * recipe_machine_stage$thisRecipesManager.getDefaultScale());
                    BaseDynamoFuel fuel = new BaseDynamoFuel(energy, inputItems, inputFluids);

                    if(fuel instanceof IThermalRecipeAddition recipeAddition) {
                        recipeAddition.setRecipeType(recipe_machine_stage$recipe.getType());
                        recipeAddition.setRecipeID(recipe_machine_stage$recipe.getId());
                    }

                    this.fuelMap.put(AbstractManager.makeNBTComparable(input), fuel);
                }
            }
        }
    }
}
