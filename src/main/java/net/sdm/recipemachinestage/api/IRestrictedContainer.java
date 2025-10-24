package net.sdm.recipemachinestage.api;

import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface IRestrictedContainer {

    List<Integer> recipe_machine_stage$getOutputSlots();

    default void recipe_machine_stage$SendStage(Player player, String stage) {
        if(this instanceof IContainerMenuSync sync) {
            sync.sdm$syncStage(player, stage, true);
        } else {
            throw new RuntimeException("Current container is not support IContainerMenuSync!");
        }
    }
}
