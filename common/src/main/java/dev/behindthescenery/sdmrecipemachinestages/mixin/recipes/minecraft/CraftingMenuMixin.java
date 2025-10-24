package dev.behindthescenery.sdmrecipemachinestages.mixin.recipes.minecraft;

import dev.behindthescenery.sdmrecipemachinestages.exceptions.IsNotServerPlayerException;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CraftingMenu.class)
public class CraftingMenuMixin {

    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "HEAD"), cancellable = true)
    private static void sdm$restriction_crafting_menu(AbstractContainerMenu abstractContainerMenu, Level level, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer, RecipeHolder<CraftingRecipe> recipeHolder, CallbackInfo ci) {
        if (level.isClientSide) return;

        final CraftingInput craftingInput = craftingContainer.asCraftInput();
        final Optional<RecipeHolder<CraftingRecipe>> currentRecipe = level.getServer()
                .getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInput, level, recipeHolder);

        if (currentRecipe.isEmpty())  return;
        final RecipeHolder<CraftingRecipe> recipe = currentRecipe.get();

        if(!RMSUtils.canProcess(player, recipe)) {
            if(!(player instanceof ServerPlayer serverPlayer)) {
                throw new IsNotServerPlayerException();
            }

            resultContainer.setItem(0, ItemStack.EMPTY);
            abstractContainerMenu.setRemoteSlot(0, ItemStack.EMPTY);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(abstractContainerMenu.containerId, abstractContainerMenu.incrementStateId(), 0, ItemStack.EMPTY));
            ci.cancel();
        }
    }
}
