package net.sdm.recipemachinestage.capability;


import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class BlockOwnerCapability implements IOwnerBlock {

    public UUID owner;


    @Override
    public void setOwner(UUID uuid) {
        this.owner = uuid;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    public void write(CompoundTag tag) {
        if (owner != null) {
            tag.putUUID("owner", owner);
        }
    }

    public void read(CompoundTag tag) {
        if (tag.contains("owner")) {
            owner = tag.getUUID("owner");
        }
    }
}
