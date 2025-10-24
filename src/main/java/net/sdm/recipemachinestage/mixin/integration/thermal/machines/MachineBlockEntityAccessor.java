package net.sdm.recipemachinestage.mixin.integration.thermal.machines;

import cofh.thermal.lib.common.block.entity.MachineBlockEntity;
import cofh.thermal.lib.util.recipes.internal.IMachineRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MachineBlockEntity.class)
public interface MachineBlockEntityAccessor {

    @Accessor(value = "curRecipe")
    IMachineRecipe getCurRecipe();
    @Accessor(value = "curRecipe")
    void setCurRecipe(IMachineRecipe curRecipe);
}
