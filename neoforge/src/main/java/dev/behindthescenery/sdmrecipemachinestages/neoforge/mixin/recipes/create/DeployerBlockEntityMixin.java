package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockEntityCustomData;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(DeployerBlockEntity.class)
public abstract class DeployerBlockEntityMixin extends KineticBlockEntity implements BlockEntityCustomData {

    @Shadow
    protected DeployerFakePlayer player;

    protected DeployerBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Inject(method = "getRecipe", at = @At("RETURN"), cancellable = true)
    public void bts$getRecipe(ItemStack stack, CallbackInfoReturnable<RecipeHolder<? extends Recipe<? extends RecipeInput>>> cir) {
        cir.setReturnValue(RMSUtils.ifCanProcessReturnRecipeOrNull(this, cir.getReturnValue()));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
    }

    @Redirect(method = "initHandler", at = @At(value = "NEW", target = "(Lnet/minecraft/server/level/ServerLevel;Ljava/util/UUID;)Lcom/simibubi/create/content/kinetics/deployer/DeployerFakePlayer;"))
    public DeployerFakePlayer bts$initHandler(ServerLevel world, UUID owner) {
        return new DeployerFakePlayer(world, RMSUtils.getBlockOwner(this));
    }
}
