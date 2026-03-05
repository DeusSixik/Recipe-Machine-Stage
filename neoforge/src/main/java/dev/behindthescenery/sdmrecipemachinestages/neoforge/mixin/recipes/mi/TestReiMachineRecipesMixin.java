package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.mi;

import aztech.modern_industrialization.compat.rei.machines.MachineCategoryParams;
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReiMachineRecipes.class)
public class TestReiMachineRecipesMixin {

//    @Inject(method = "registerCategory", at = @At("HEAD"))
//    private static void bts$m2(ResourceLocation machine, MachineCategoryParams params, CallbackInfo ci) {
//        throw new RuntimeException("Where you call ?");
//    }
}
