package net.sdm.recipemachinestage.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;

public interface IContainerMenuSync {

    DataSlot sdm$addDataSlot(DataSlot p_38896_);

    default void sdm$syncStage(Player player, String stage) {
        sdm$syncStage(player,stage, false);
    }

    void sdm$setStage(String stage);
    void sdm$syncStage(Player player, String stage, boolean packet);
    String sdm$getStage();
}
