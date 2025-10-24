package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create;

import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.foundation.item.CustomUseEffectsItem;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.utils.RMSCreateUtils;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SandPaperItem.class)
public abstract class SandPaperItemMixin extends Item implements CustomUseEffectsItem {

    protected SandPaperItemMixin(Properties arg) {
        super(arg);
    }


    @Unique
    @Nullable
    private Player bts$player;

    @Inject(method = "use", at = @At("HEAD"))
    public void bts$use(Level worldIn, Player playerIn, InteractionHand handIn, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        bts$player = playerIn;
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/equipment/sandPaper/SandPaperPolishingRecipe;canPolish(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean bts$edirect(Level world, ItemStack stack) {
        if(bts$player == null) return SandPaperPolishingRecipe.canPolish(world, stack);
        return RMSCreateUtils.canPolish(world, stack, RMSUtils.getPlayerId(bts$player));
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    public void bts$use(ItemStack stack, Level worldIn, LivingEntity entityLiving, CallbackInfoReturnable<ItemStack> cir) {
        bts$player = entityLiving instanceof Player player ? player : null;
    }

    @Redirect(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/equipment/sandPaper/SandPaperPolishingRecipe;applyPolish(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack bts$redirect(Level world, Vec3 position, ItemStack stack, ItemStack sandPaperStack) {
        if(bts$player == null) return SandPaperPolishingRecipe.applyPolish(world, position, stack, sandPaperStack);

        return RMSCreateUtils.applyPolish(world, position, stack, sandPaperStack, RMSUtils.getPlayerId(bts$player));
    }
}
