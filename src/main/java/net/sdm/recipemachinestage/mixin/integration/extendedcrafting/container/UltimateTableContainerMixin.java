package net.sdm.recipemachinestage.mixin.integration.extendedcrafting.container;

import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.container.UltimateTableContainer;
import com.blakebr0.extendedcrafting.container.inventory.ExtendedCraftingInventory;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sdm.recipemachinestage.api.IRestrictedContainer;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;


@Mixin(UltimateTableContainer.class)
public class UltimateTableContainerMixin implements IRestrictedContainer {

    @Shadow
    @Final
    private Level level;
    @Shadow @Final private Container result;

    @Unique
    private Inventory recipe_machine_stage$playerInventory;

    @Unique
    private UltimateTableContainer recipe_machine_stage$thisContainer = RecipeStagesUtil.cast(this);

    private String sdm$thisStage = "";

    @Inject(method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lcom/blakebr0/cucumber/inventory/BaseItemStackHandler;Lnet/minecraft/core/BlockPos;)V", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void sdm$const(MenuType type, int id, Inventory playerInventory, BaseItemStackHandler inventory, BlockPos pos, CallbackInfo ci, ExtendedCraftingInventory matrix, int i, int j) {
        this.recipe_machine_stage$playerInventory = playerInventory;
        recipe_machine_stage$thisContainer.slotsChanged(matrix);
    }

    @Inject(method = "slotsChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 0), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void sdm$slotsChanged(Container matrix, CallbackInfo ci, Optional recipeOptional, ItemStack result) {
        ITableRecipe recipe = (ITableRecipe) recipeOptional.get();
        if(!StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() && StageContainer.INSTANCE.RECIPES_STAGES.containsKey(ModRecipeTypes.TABLE.get())) {
            RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if (recipeBlockType != null && recipe_machine_stage$playerInventory != null) {
                sdm$thisStage = recipeBlockType.stage;
                if (!PlayerHelper.hasStage(recipe_machine_stage$playerInventory.player, recipeBlockType.stage)) {
                    ci.cancel();
                    sdm$uppdate(ItemStack.EMPTY, sdm$thisStage);
                }
            }
        }
    }

    private void sdm$uppdate(ItemStack item, String stage) {
        this.result.setItem(0, item);
        sdm$uppdate(stage);
    }

    private void sdm$uppdate(ITableRecipe recipe, Container matrix, String stage) {
        ItemStack result = recipe.assemble( matrix, this.level.registryAccess());
        this.result.setItem(0, result);
        sdm$uppdate(stage);
    }

    private void sdm$uppdate(String stage) {

        if(recipe_machine_stage$playerInventory != null) {
            recipe_machine_stage$SendStage(recipe_machine_stage$playerInventory.player, stage);
        }
    }



    @Override
    public List<Integer> recipe_machine_stage$getOutputSlots() {
        return List.of(0);
    }
}
