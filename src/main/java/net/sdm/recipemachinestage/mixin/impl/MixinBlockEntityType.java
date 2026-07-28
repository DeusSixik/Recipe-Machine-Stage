package net.sdm.recipemachinestage.mixin.impl;


import net.minecraft.world.level.block.entity.BlockEntityType;
import net.sdm.recipemachinestage.impl.patches.BlockEntityTypeIndex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntityType.class)
public class MixinBlockEntityType implements BlockEntityTypeIndex {

    @Unique
    private int gpr$index = -1;

    @Override
    public int gpr$getIndex() {
        return gpr$index;
    }

    @Override
    public void gpr$setIndex(int index) {
        this.gpr$index = index;
    }
}
