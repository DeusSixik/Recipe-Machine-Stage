package net.sdm.recipemachinestage.client;

import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.darkhax.gamestages.data.IStageData;
import net.darkhax.gamestages.event.StagesSyncedEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.sdm.recipemachinestage.compat.jei.JEIPlugin;

public class ClientEventHandler {

    @SubscribeEvent
    public void onGamestageSync(StagesSyncedEvent event) {
        if(ModList.get().isLoaded("jei")) {
            Minecraft.getInstance().execute(() -> {
                JEIPlugin.sync(event.getData());
            });
        }

    }

    @SubscribeEvent
    public void recipes(RecipesUpdatedEvent event) {
        if(ModList.get().isLoaded("jei")) {
            Minecraft.getInstance().execute(() -> {
                IStageData data = GameStageSaveHandler.getClientData();
                if(data == null) {
                    return;
                }
                JEIPlugin.sync(data);
            });
        }

    }
}
