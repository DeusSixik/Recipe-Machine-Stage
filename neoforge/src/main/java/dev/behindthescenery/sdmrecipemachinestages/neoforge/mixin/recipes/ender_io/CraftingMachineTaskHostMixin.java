package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.ender_io;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.machines.common.blocks.base.MachineRecipe;
import com.enderio.machines.common.blocks.base.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.blocks.base.task.host.MachineTaskHost;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(CraftingMachineTaskHost.class)
public abstract class CraftingMachineTaskHostMixin<R extends MachineRecipe<T>, T extends RecipeInput> extends MachineTaskHost {

    @Shadow
    @Final
    private RecipeType<R> recipeType;

    @Shadow
    @Final
    private Supplier<T> recipeInputSupplier;

    @Unique
    private EnderBlockEntity bts$blockEntity;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void bts$init(EnderBlockEntity blockEntity, Supplier canAcceptNewTask, RecipeType recipeType, CraftingMachineTaskHost.CraftingMachineTaskFactory taskFactory, Supplier recipeInputSupplier, CallbackInfo ci) {
        this.bts$blockEntity = blockEntity;
    }

    protected CraftingMachineTaskHostMixin(com.enderio.core.common.blockentity.EnderBlockEntity blockEntity, Supplier<Boolean> canAcceptNewTask) {
        super(blockEntity, canAcceptNewTask);
    }

    /**
     * @author Sixik
     * @reason Change logic
     */
    @Overwrite
    protected Optional<RecipeHolder<R>> findRecipe() {
        final Level level = this.getLevel();
        if(level == null) return Optional.empty();

        final Optional<RecipeHolder<R>> find = level.getRecipeManager().getRecipeFor(this.recipeType, this.recipeInputSupplier.get(), level);
        if(find.isEmpty()) return Optional.empty();
        final RecipeHolder<R> getting = find.get();
        return RMSUtils.canProcess(bts$blockEntity, getting) ? find : Optional.empty();
    }
}
