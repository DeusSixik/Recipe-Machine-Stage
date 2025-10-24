package net.sdm.recipemachinestage.api.capability;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.UUID;

@AutoRegisterCapability
public interface IOwnerBlock {
    void setOwner(UUID uuid);
    UUID getOwner();
}
