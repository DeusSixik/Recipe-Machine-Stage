package net.sdm.recipemachinestage.mixin.integration.bloodmagic;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RMSUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wayoftime.bloodmagic.altar.BloodAltar;
import wayoftime.bloodmagic.common.tile.TileAltar;
import wayoftime.bloodmagic.impl.BloodMagicRecipeRegistrar;
import wayoftime.bloodmagic.recipe.RecipeBloodAltar;

import java.util.Optional;

@Mixin(value = BloodAltar.class, remap = false)
public class BloodAltarMixin {

    @Shadow private TileAltar tileAltar;

    @Redirect(method = "startCycle", at = @At(value = "INVOKE", target = "Lwayoftime/bloodmagic/impl/BloodMagicRecipeRegistrar;getBloodAltar(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lwayoftime/bloodmagic/recipe/RecipeBloodAltar;"))
    private RecipeBloodAltar sdm$startCycle(BloodMagicRecipeRegistrar instance, Level level, ItemStack world) {
        final RecipeBloodAltar recipe = instance.getBloodAltar(level, world);
        return RMSUtils.canRecipe(recipe, tileAltar) ? recipe : null;
    }
}
