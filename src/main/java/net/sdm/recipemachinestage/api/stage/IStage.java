package net.sdm.recipemachinestage.api.stage;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;

import java.util.Collection;

public interface IStage {

    @OnlyIn(Dist.CLIENT)
    Collection<String> getStages();

    @OnlyIn(Dist.CLIENT)
    boolean hasStage(String stage);

    String getModID();

    default boolean isModLoaded() {
        return ModList.get().isLoaded(getModID());
    }

    /**
     * Allows you to register events that are registered when the component is loaded.
     */
    @OnlyIn(Dist.CLIENT)
    default void registerEventsClient() {}

    default void registerEventsServer() {}
}
