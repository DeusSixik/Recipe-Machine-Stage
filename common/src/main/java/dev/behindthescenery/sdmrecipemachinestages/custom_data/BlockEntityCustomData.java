package dev.behindthescenery.sdmrecipemachinestages.custom_data;

import dev.behindthescenery.sdmrecipemachinestages.exceptions.BlockEntityNotSupport;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface BlockEntityCustomData {

    String CUSTOM_KEY = "block_custom_data";

    CustomData sdm$getCustomData();

    static CustomData getData(BlockEntity entity) {
        return ((BlockEntityCustomData)entity).sdm$getCustomData();
    }

    static CustomData getDataOrThrow(BlockEntity entity) {
        if(entity instanceof BlockEntityCustomData customData)
            return customData.sdm$getCustomData();

        throw new BlockEntityNotSupport("Block entity don't have a BlockEntityCustomData!");
    }
}
