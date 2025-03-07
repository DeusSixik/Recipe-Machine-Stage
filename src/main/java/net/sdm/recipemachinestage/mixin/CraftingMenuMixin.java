package net.sdm.recipemachinestage.mixin;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CraftingMenu.class)
public class CraftingMenuMixin {

    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "HEAD"), cancellable = true)
    private static void sdm$slotsChanged(AbstractContainerMenu abstractContainerMenu, Level level, Player player, CraftingContainer p_150550_, ResultContainer p_150551_, CallbackInfo ci) {
        if (level.isClientSide) return;
        Optional<CraftingRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, p_150550_, level);
        if (optional.isEmpty())  return;
        CraftingRecipe recipe = optional.get();

        if (StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() && !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(recipe.getType())) return;
        RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.getType(), recipe.getId());
        if (recipeBlockType == null)  return;
        if (PlayerHelper.hasStage(player, recipeBlockType.stage))  return;

        if(player instanceof ServerPlayer serverPlayer) {
            p_150551_.setItem(0, ItemStack.EMPTY);
            abstractContainerMenu.setRemoteSlot(0, ItemStack.EMPTY);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(abstractContainerMenu.containerId, abstractContainerMenu.incrementStateId(), 0, ItemStack.EMPTY));
            ci.cancel();
        }

    }
}
