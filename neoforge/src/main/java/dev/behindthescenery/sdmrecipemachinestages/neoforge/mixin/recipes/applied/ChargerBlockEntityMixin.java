package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.applied;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnit;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.blockentity.misc.ChargerBlockEntity;
import appeng.blockentity.misc.ChargerRecipes;
import appeng.core.AEConfig;
import appeng.recipes.handlers.ChargerRecipe;
import appeng.util.Platform;
import appeng.util.inv.AppEngInternalInventory;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockOwnerData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(ChargerBlockEntity.class)
public abstract class ChargerBlockEntityMixin extends AENetworkedPoweredBlockEntity implements IGridTickable {

    @Shadow
    private boolean working;

    @Shadow
    @Final
    private AppEngInternalInventory inv;

    public ChargerBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    /**
     * @author Sixik
     * @reason Optimize operation
     */
    @Overwrite
    private void doWork(int ticksSinceLastCall) {
        final boolean wasWorking = this.working;
        this.working = false;
        boolean changed = false;

        final ItemStack stack = this.inv.getStackInSlot(0);
        if (stack.isEmpty()) {
            if (wasWorking) markForUpdate();
            return;
        }

        double internalPower = this.getInternalCurrentPower();
        final double internalMax = this.getInternalMaxPower();

        if (Platform.isChargeable(stack)) {
            final IAEItemPowerStorage ps = (IAEItemPowerStorage) stack.getItem();
            final double currentPower = ps.getAECurrentPower(stack);
            final double maxPower = ps.getAEMaxPower(stack);

            if (currentPower < maxPower) {
                final double baseCharge = ps.getChargeRate(stack)
                        * ticksSinceLastCall
                        * AEConfig.instance().getChargerChargeRate();

                final double[] extracted = {this.extractAEPower(baseCharge, Actionable.MODULATE, PowerMultiplier.CONFIG)};

                final double remaining = Math.min(baseCharge - extracted[0], maxPower - currentPower);
                if (remaining > 0) {
                    this.getMainNode().ifPresent(grid -> {
                        extracted[0] += grid.getEnergyService().extractAEPower(remaining, Actionable.MODULATE, PowerMultiplier.ONE);
                    });
                }

                if (extracted[0] > 0) {
                    final double accepted = ps.injectAEPower(stack, extracted[0], Actionable.MODULATE);
                    this.setInternalCurrentPower(internalPower + accepted);
                    this.working = true;
                    changed = true;
                    internalPower += accepted;
                }
            }
        }

        else if (internalPower >= 1599.0 && this.level != null) {

            RMSMain.setBlockOwner(BlockOwnerData.getOrThrow(this));
            final ChargerRecipe recipe = ChargerRecipes.findRecipe(this.level, stack);
            if (recipe != null) {
                this.working = true;

                if (this.level.random.nextFloat() > 0.8F) {
                    this.extractAEPower(internalMax, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    this.inv.setItemDirect(0, recipe.result.copy());
                    changed = true;
                }
            }
        }

        if (internalPower < 1599.0) {
            final double finalInternalPower = internalPower;
            this.getMainNode().ifPresent(grid -> {
                final double toExtract = Math.min(800.0, internalMax - finalInternalPower);
                final double extracted = grid.getEnergyService().extractAEPower(toExtract, Actionable.MODULATE, PowerMultiplier.ONE);
                this.injectExternalPower(PowerUnit.AE, extracted, Actionable.MODULATE);
            });
            changed = true;
        }

        if (changed || this.working != wasWorking) {
            this.markForUpdate();
        }
    }
}
