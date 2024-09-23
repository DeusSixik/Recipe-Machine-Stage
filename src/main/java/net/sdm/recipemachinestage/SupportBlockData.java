package net.sdm.recipemachinestage;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.sdm.recipemachinestage.capability.IOwnerBlock;

public class SupportBlockData {

    public static final Capability<IOwnerBlock> BLOCK_OWNER = CapabilityManager.get(new CapabilityToken<>(){});

    public static void init() {

    }
}
