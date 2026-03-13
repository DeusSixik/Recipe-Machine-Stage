package dev.behindthescenery.sdmrecipemachinestages.mixin.recipes.minecraft;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin extends BlockEntity {

    @Unique
    private static final ThreadLocal<CampfireBlockEntity> CAMPFIRE_BLOCK_ENTITY_THREAD_LOCAL = new ThreadLocal<>();

    public CampfireBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "cookTick", at = @At("HEAD"))
    private static void bts$tick(Level level, BlockPos blockPos, BlockState blockState, CampfireBlockEntity campfireBlockEntity, CallbackInfo ci) {
        CAMPFIRE_BLOCK_ENTITY_THREAD_LOCAL.set(campfireBlockEntity);
    }

    @Redirect(method = "cookTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private static <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> bts$tick$inject(RecipeManager.CachedCheck<I, T> instance, I i, Level level) {
        final Optional<RecipeHolder<T>> recipeOpt = instance.getRecipeFor(i, level);
        if(recipeOpt.isEmpty()) return recipeOpt;
        return RMSUtils.canProcess(CAMPFIRE_BLOCK_ENTITY_THREAD_LOCAL.get(), recipeOpt.get()) ? recipeOpt : Optional.empty();
    }
}
