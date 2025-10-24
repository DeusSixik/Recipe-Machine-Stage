package net.sdm.recipemachinestage.mixin.integration.thermal.manager;

import cofh.lib.util.crafting.ComparableItemStack;
import cofh.thermal.lib.util.managers.AbstractManager;
import cofh.thermal.lib.util.managers.SingleItemRecipeManager;
import cofh.thermal.lib.util.recipes.ThermalRecipe;
import cofh.thermal.lib.util.recipes.internal.BaseMachineRecipe;
import cofh.thermal.lib.util.recipes.internal.DisenchantMachineRecipe;
import cofh.thermal.lib.util.recipes.internal.IMachineRecipe;
import cofh.thermal.lib.util.recipes.internal.SimpleMachineRecipe;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(value = SingleItemRecipeManager.class, remap = false)
public class SingleItemRecipeManagerMixin {

    @Shadow protected Map<ComparableItemStack, IMachineRecipe> recipeMap;
    @Shadow protected int maxOutputItems;
    @Shadow protected int maxOutputFluids;
    @Unique
    private ThermalRecipe recipe_machine_stage$recipe;

    @Unique
    private SingleItemRecipeManager custom$manager = RecipeStagesUtil.cast(this);

//
//    @Inject(method = "addRecipe(Lcofh/thermal/lib/util/recipes/ThermalRecipe;Lcofh/thermal/lib/util/recipes/internal/BaseMachineRecipe$RecipeType;)V", at = @At("HEAD"))
//    private void sdm$addRecipe(ThermalRecipe recipe, BaseMachineRecipe.RecipeType type, CallbackInfo ci){
//    }

    @Inject(method = "addRecipe(Lcofh/thermal/lib/util/recipes/ThermalRecipe;Lcofh/thermal/lib/util/recipes/internal/BaseMachineRecipe$RecipeType;)V", at = @At(value = "INVOKE", target = "Lcofh/thermal/lib/util/managers/SingleItemRecipeManager;addRecipe(IFLjava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lcofh/thermal/lib/util/recipes/internal/BaseMachineRecipe$RecipeType;)Lcofh/thermal/lib/util/recipes/internal/IMachineRecipe;", ordinal = 0), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void sdm$addRecipe$1(ThermalRecipe recipe, BaseMachineRecipe.RecipeType type, CallbackInfo ci, FluidStack[] var3, int var4, int var5, FluidStack fluidInput, ItemStack[] var7, int var8, int var9, ItemStack recipeInput) {
        ci.cancel();
        this.recipe_machine_stage$recipe = recipe;
        this.sdm$CustomaddRecipe(recipe.getEnergy(), recipe.getXp(), Collections.singletonList(recipeInput), Collections.singletonList(fluidInput), recipe.getOutputItems(), recipe.getOutputItemChances(), recipe.getOutputFluids(), type);
    }

    @Inject(method = "addRecipe(Lcofh/thermal/lib/util/recipes/ThermalRecipe;Lcofh/thermal/lib/util/recipes/internal/BaseMachineRecipe$RecipeType;)V", at = @At(value = "INVOKE", target = "Lcofh/thermal/lib/util/managers/SingleItemRecipeManager;addRecipe(IFLjava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lcofh/thermal/lib/util/recipes/internal/BaseMachineRecipe$RecipeType;)Lcofh/thermal/lib/util/recipes/internal/IMachineRecipe;", ordinal = 1), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void sdm$addRecipe$2(ThermalRecipe recipe, BaseMachineRecipe.RecipeType type, CallbackInfo ci, ItemStack[] var3, int var4, int var5, ItemStack recipeInput) {
        ci.cancel();
        this.recipe_machine_stage$recipe = recipe;
        this.sdm$CustomaddRecipe(recipe.getEnergy(), recipe.getXp(), Collections.singletonList(recipeInput), Collections.emptyList(), recipe.getOutputItems(), recipe.getOutputItemChances(), recipe.getOutputFluids(), type);
    }

    @Unique
    protected void sdm$CustomaddRecipe(int energy, float experience, List<ItemStack> inputItems, List<FluidStack> inputFluids, List<ItemStack> outputItems, List<Float> chance, List<FluidStack> outputFluids, BaseMachineRecipe.RecipeType type) {
        if (!inputItems.isEmpty() && (!outputItems.isEmpty() || !outputFluids.isEmpty()) && outputItems.size() <= this.maxOutputItems && outputFluids.size() <= this.maxOutputFluids && energy > 0) {
            ItemStack input = (ItemStack)inputItems.get(0);
            if (input.isEmpty()) {
            } else {
                Iterator var10 = outputItems.iterator();

                ItemStack stack;
                do {
                    if (!var10.hasNext()) {
                        var10 = outputFluids.iterator();

                        FluidStack stack1;
                        do {
                            if (!var10.hasNext()) {
                                energy = (int)((float)energy * custom$manager.getDefaultScale());

                                BaseMachineRecipe recipe;

                                if (type == BaseMachineRecipe.RecipeType.DISENCHANT) {
                                    recipe = new DisenchantMachineRecipe(energy, experience, inputItems, inputFluids, outputItems, chance, outputFluids);

                                    if(recipe instanceof IThermalRecipeAddition addition) {
                                        addition.setRecipeID(this.recipe_machine_stage$recipe.getId());
                                        addition.setRecipeType(this.recipe_machine_stage$recipe.getType());
                                    }

                                    this.recipeMap.put(AbstractManager.makeComparable(input), recipe);
                                } else {
                                    recipe = new SimpleMachineRecipe(energy, experience, inputItems, inputFluids, outputItems, chance, outputFluids);

                                    if(recipe instanceof IThermalRecipeAddition addition) {
                                        addition.setRecipeID(this.recipe_machine_stage$recipe.getId());
                                        addition.setRecipeType(this.recipe_machine_stage$recipe.getType());
                                    }

                                    this.recipeMap.put(AbstractManager.makeNBTComparable(input), recipe);
                                }

                                return;
                            }

                            stack1 = (FluidStack)var10.next();
                        } while(!stack1.isEmpty());

                        return;
                    }

                    stack = (ItemStack)var10.next();
                } while(!stack.isEmpty());

            }
        }
    }

}
