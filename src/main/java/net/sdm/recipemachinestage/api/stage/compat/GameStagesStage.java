package net.sdm.recipemachinestage.api.stage.compat;

import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.darkhax.gamestages.event.GameStageEvent;
import net.darkhax.gamestages.event.StagesSyncedEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.sdm.recipemachinestage.api.stage.IStage;
import net.sdm.recipemachinestage.compat.jei.JEIPlugin;
import net.sdm.recipemachinestage.utils.PlayerHelper;

import java.util.Collection;
import java.util.List;

public class GameStagesStage implements IStage {

    public GameStagesStage() {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public Collection<String> getStages() {
        IStageData data = GameStageHelper.getPlayerData(Minecraft.getInstance().player);
        if(data == null) return List.of();

        return data.getStages();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasStage(String stage) {
        IStageData data = GameStageHelper.getPlayerData(Minecraft.getInstance().player);
        return data != null && data.hasStage(stage);
    }

    @Override
    public String getModID() {
        return "gamestages";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerEventsClient() {
        MinecraftForge.EVENT_BUS.addListener(this::syncStageEvent);
    }

    @OnlyIn(Dist.CLIENT)
    protected void syncStageEvent(StagesSyncedEvent event) {
        if(ModList.get().isLoaded("jei")) {
            Minecraft.getInstance().execute(JEIPlugin::sync);
        }
    }

    protected void OnAdded(GameStageEvent.Added event) {
        PlayerHelper.UpdateData(event.getEntity());
    }

    protected void OnRemoved(GameStageEvent.Removed event) {
        PlayerHelper.UpdateData(event.getEntity());
    }

    @Override
    public void registerEventsServer() {
        MinecraftForge.EVENT_BUS.addListener(this::OnAdded);
        MinecraftForge.EVENT_BUS.addListener(this::OnRemoved);
    }
}
