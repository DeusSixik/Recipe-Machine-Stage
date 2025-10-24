package net.sdm.recipemachinestage;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;

public class RMSCapability {

    public static final Capability<IOwnerBlock> BLOCK_OWNER = CapabilityManager.get(new CapabilityToken<>(){});


}
