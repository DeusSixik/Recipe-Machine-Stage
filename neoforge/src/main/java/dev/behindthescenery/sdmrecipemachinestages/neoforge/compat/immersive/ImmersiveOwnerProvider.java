package dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive;

import dev.behindthescenery.sdmrecipemachinestages.exceptions.BlockNotSupportStageException;

import java.util.UUID;

public interface ImmersiveOwnerProvider {

    String KEY = "sdm_owner_block_key";

    default void sdm$setOwner(UUID ownerId) { }

    default UUID sdm$getOwner() {
        throw new BlockNotSupportStageException();
    }

    static <T> void sdm$setOwner(T element, UUID owner) {
        if(element instanceof ImmersiveOwnerProvider ownerProvider) {
            ownerProvider.sdm$setOwner(owner);
            return;
        }

        throw new RuntimeException("Can't set owner! " + element.getClass().getName());
    }

    static <T> UUID sdm$getOwner(T element) {
        if(element instanceof ImmersiveOwnerProvider provider)
            return provider.sdm$getOwner();

        throw new RuntimeException("Can't get owner! " + element.getClass().getName());
    }
}
