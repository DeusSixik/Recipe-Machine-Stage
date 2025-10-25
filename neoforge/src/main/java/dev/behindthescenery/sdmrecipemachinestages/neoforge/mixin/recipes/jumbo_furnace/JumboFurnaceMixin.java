package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.jumbo_furnace;

import com.mojang.datafixers.util.Pair;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockOwnerData;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.commoble.jumbofurnace.JumboFurnace;
import net.commoble.jumbofurnace.advancements.AssembleJumboFurnaceTrigger;
import net.commoble.jumbofurnace.jumbo_furnace.MultiBlockHelper;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(JumboFurnace.class)
public abstract class JumboFurnaceMixin {

    @Shadow
    @Final
    public static TagKey<Block> JUMBOFURNACEABLE_TAG;

    @Shadow
    @Final
    public DeferredHolder<CriterionTrigger<?>, AssembleJumboFurnaceTrigger> assembleJumboFurnaceTrigger;

    @Shadow
    private static void addItemsToList(List<ItemStack> stacks, IItemHandler handler) {
        throw new NotImplementedException();
    }

    /**
     * @author Sixik
     * @reason Change Logic
     */
    @Overwrite
    private void onEntityPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        final BlockState state = event.getPlacedBlock();
        final LevelAccessor levelAccess = event.getLevel();
        if (!(event instanceof BlockEvent.EntityMultiPlaceEvent) && state.is(JUMBOFURNACEABLE_TAG) && levelAccess instanceof Level level) {
            final BlockPos pos = event.getPos();
            final BlockState againstState = event.getPlacedAgainst();
            final Entity entity = event.getEntity();
            final List<ItemStack> stacks = new ArrayList();
            final List<Pair<BlockPos, BlockState>> pairs = MultiBlockHelper.getJumboFurnaceStates(((Level)levelAccess).dimension(), levelAccess, pos, againstState, entity);
            if (!pairs.isEmpty()) {
                for(Pair<BlockPos, BlockState> pair : pairs) {
                    final BlockPos newPos = pair.getFirst();
                    final BlockState newState = pair.getSecond();

                    for(final Direction dir : Direction.values()) {
                        final IItemHandler sideHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, newPos, dir);
                        if (sideHandler != null) {
                            addItemsToList(stacks, sideHandler);
                        }
                    }

                    final IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, newPos, null);
                    if (handler != null) {
                        addItemsToList(stacks, handler);
                    }

                    levelAccess.setBlock(newPos, newState, 3);
                }

                if (entity instanceof ServerPlayer player) {
                    this.assembleJumboFurnaceTrigger.get().trigger(player);
                    RMSUtils.setBlockEntityOwner(level.getBlockEntity(event.getPos()), player);
                }
            }

            if (!stacks.isEmpty()) {
                if (entity instanceof Player) {
                    for(ItemStack stack : stacks) {
                        ((Player)entity).addItem(stack);
                    }
                } else {
                    for(ItemStack stack : stacks) {
                        entity.spawnAtLocation(stack);
                    }
                }
            }
        }

    }
}
