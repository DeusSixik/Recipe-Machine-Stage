package net.sdm.recipemachinestage.impl.patches;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface BlockEntityTypeIndex {

    static BlockEntityTypeIndex get(BlockEntity block) {
        return get(block.getType());
    }

    static BlockEntityTypeIndex get(BlockEntityType<?> entityType) {
        if(entityType instanceof BlockEntityTypeIndex index)
            return index;
        throw new IllegalArgumentException("Not a BlockEntityTypeIndex");
    }

    int gpr$getIndex();

    void gpr$setIndex(int index);
}