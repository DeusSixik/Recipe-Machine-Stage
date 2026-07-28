package net.sdm.recipemachinestage.mixin.integration.applied;

import appeng.blockentity.misc.ChargerBlockEntity;
import appeng.blockentity.misc.ChargerRecipes;
import appeng.recipes.handlers.ChargerRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChargerBlockEntity.class, remap = false)
public class ChargerBlockEntityMixin {

    private ChargerBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Redirect(method = "doWork", at = @At
        (
        value = "INVOKE",
        ordinal = 0,
        target = "Lappeng/blockentity/misc/ChargerRecipes;findRecipe(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lappeng/recipes/handlers/ChargerRecipe;"
        )
    )
    public ChargerRecipe sdm$doWork(Level level, ItemStack myItem){
        ChargerRecipe recipe = ChargerRecipes.findRecipe(level, myItem);
        return RecipeStagesUtil.checkRecipe(recipe, thisEntity);
    }
}
