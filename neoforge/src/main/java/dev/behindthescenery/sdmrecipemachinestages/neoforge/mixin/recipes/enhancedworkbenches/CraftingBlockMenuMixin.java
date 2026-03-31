package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.enhancedworkbenches;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import me.luligabi.enhancedworkbenches.common.common.menu.CraftingBlockMenu;
import me.luligabi.enhancedworkbenches.common.common.menu.DelegateCraftingInventory;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(CraftingBlockMenu.class)
public class CraftingBlockMenuMixin {

    /**
     * @author Sixik
     * @reason Add restriction to logic
     */
    @Overwrite
    protected static void updateResult(AbstractContainerMenu menu, Level level, Player player, DelegateCraftingInventory input, ResultContainer output) {
        if (!level.isClientSide()) {
            ServerPlayer serverPlayerEntity = (ServerPlayer)player;
            ItemStack itemStack = ItemStack.EMPTY;
            Optional<RecipeHolder<CraftingRecipe>> recipeOptional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input.toCraftingInput(), level);
            if (recipeOptional.isPresent()) {
                RecipeHolder<CraftingRecipe> recipe = recipeOptional.get();

                if (
                    /* RMS START */
                    RMSUtils.canProcess(player, recipe) &&
                    /* RMS END */
                    output.setRecipeUsed(level, serverPlayerEntity, recipe)
                ) {
                    ItemStack itemStack2 = recipe.value().assemble(input.toCraftingInput(), level.registryAccess());
                    if (itemStack2.isItemEnabled(level.enabledFeatures())) {
                        itemStack = itemStack2;
                    }
                }
            }

            output.setItem(0, itemStack);
            menu.setRemoteSlot(0, itemStack);
            serverPlayerEntity.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, itemStack));
        }
    }
}
