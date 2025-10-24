package dev.behindthescenery.sdmrecipemachinestages.custom_data;

import dev.behindthescenery.sdmrecipemachinestages.exceptions.BlockEntityNotSupportException;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

public interface BlockEntityCustomData {

    String CUSTOM_KEY = "block_custom_data";

    CustomData sdm$getCustomData();

    default void sdm$setOwner(UUID uuid) {
        sdm$getCustomData().putData(BlockOwnerData.OWNER_KEY, uuid);
    }

    static BlockEntityCustomData getBlockData(BlockEntity entity) {
        return (BlockEntityCustomData)entity;
    }

    static CustomData getData(BlockEntity entity) {
        return ((BlockEntityCustomData)entity).sdm$getCustomData();
    }

    static CustomData getDataOrThrow(BlockEntity entity) {
        if(entity instanceof BlockEntityCustomData customData)
            return customData.sdm$getCustomData();

        throw new BlockEntityNotSupportException("Block entity don't have a BlockEntityCustomData!");
    }
}
