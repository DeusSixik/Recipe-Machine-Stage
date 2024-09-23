package net.sdm.recipemachinestage.capability;

import java.util.UUID;

public interface IOwnerableSupport {

    boolean recipe_machine_stage$isSupported();

    void recipe_machine_stage$setOwner(UUID owner);
    UUID recipe_machine_stage$getOwner();
    void recipe_machine_stage$setOwnerCapability(BlockOwnerCapability owner);
    BlockOwnerCapability recipe_machine_stage$getBlockOwnerCapability();
}
