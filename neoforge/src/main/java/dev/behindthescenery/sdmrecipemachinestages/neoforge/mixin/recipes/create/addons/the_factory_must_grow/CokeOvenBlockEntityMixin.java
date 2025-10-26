package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create.addons.the_factory_must_grow;

import com.drmangotea.tfmg.content.machinery.metallurgy.coke_oven.CokeOvenBlockEntity;
import com.drmangotea.tfmg.recipes.CokingRecipe;
import com.drmangotea.tfmg.registry.TFMGRecipeTypes;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.item.SmartInventory;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(value = CokeOvenBlockEntity.class)
public abstract class CokeOvenBlockEntityMixin extends SmartBlockEntity {

    @Shadow
    public SmartInventory inventory;

    @Shadow
    private int timer;

    @Shadow
    public abstract void onContentsChanged();

    @Shadow
    public FluidTank primaryTank;

    @Shadow
    public FluidTank secondaryTank;

    protected CokeOvenBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * @author Sixik
     * @reason Change logic
     */
    @Overwrite
    public void tickRecipe() {
        if (!this.inventory.isEmpty() && this.timer != -1) {
            Optional<RecipeHolder<CokingRecipe>> optional = TFMGRecipeTypes.COKING.find(new RecipeWrapper(this.inventory), this.level);
            if (optional.isEmpty()) {
                this.timer = -1;
            } else {
                final RecipeHolder<?> holder = optional.get();

                if(!RMSUtils.canProcess(this, holder)) {
                    this.timer = -1;
                    return;
                }
                final CokingRecipe recipe = (CokingRecipe)holder.value();
                if (this.timer == 0) {
                    this.timer = -1;
                    this.inventory.getItem(0).shrink(((Ingredient)recipe.getIngredients().get(0)).getItems()[0].getCount());
                    Direction direction = (Direction)this.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
                    Vec3 dropVec = VecHelper.getCenterOf(this.worldPosition.relative(direction)).add((double)0.0F, 0.4, (double)0.0F);
                    ItemEntity dropped = new ItemEntity(this.level, dropVec.x, dropVec.y, dropVec.z, recipe.getResultItem(this.level.registryAccess()).copy());
                    dropped.setDefaultPickUpDelay();
                    dropped.setDeltaMovement(direction.getAxis() == Direction.Axis.X ? (direction == Direction.WEST ? (double)-0.01F : (double)0.01F) : (double)0.0F, (double)0.05F, direction.getAxis() == Direction.Axis.Z ? (direction == Direction.NORTH ? (double)-0.01F : (double)0.01F) : (double)0.0F);
                    this.level.addFreshEntity(dropped);
                    if (!this.level.isClientSide) {
                        this.setChanged();
                        this.sendData();
                    }

                    this.onContentsChanged();
                }

                if (this.timer > 0 && this.primaryTank.getSpace() != 0 && this.secondaryTank.getSpace() != 0) {
                    this.primaryTank.fill(recipe.getPrimaryResult(), IFluidHandler.FluidAction.EXECUTE);
                    this.secondaryTank.fill(recipe.getSecondaryResult(), IFluidHandler.FluidAction.EXECUTE);
                    --this.timer;
                }

            }
        }
    }
}
