package net.sdm.recipemachinestage.api.stage.compat;

import com.alessandro.astages.api.event.player.StageAddedPlayerEvent;
import com.alessandro.astages.api.event.player.StageRemovedPlayerEvent;
import com.alessandro.astages.api.event.sync.ClientSynchronizeStagesEvent;
import com.alessandro.astages.api.holder.AClientHolder;
import com.alessandro.astages.api.util.AStagesClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.sdm.recipemachinestage.api.stage.IStage;
import net.sdm.recipemachinestage.compat.jei.JEIPlugin;
import net.sdm.recipemachinestage.utils.PlayerHelper;

import java.util.Collection;

public class AStagesStage implements IStage {

    @Override
    @OnlyIn(Dist.CLIENT)
    public Collection<String> getStages() {
        return AStagesClientUtils.getStages(AClientHolder.serverAndPlayer());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasStage(String stage) {
        return AStagesClientUtils.hasStage(AClientHolder.serverAndPlayer(), stage);
    }

    @Override
    public String getModID() {
        return "astages";
    }

    @Override
    public void registerEventsServer() {
        MinecraftForge.EVENT_BUS.addListener(this::OnAdd);
        MinecraftForge.EVENT_BUS.addListener(this::OnRemove);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerEventsClient() {
        MinecraftForge.EVENT_BUS.<ClientSynchronizeStagesEvent>addListener((event) -> {
            if(ModList.get().isLoaded("jei")) {
                Minecraft.getInstance().execute(JEIPlugin::sync);
            }
        });
    }

    protected void OnAdd(StageAddedPlayerEvent event) {
        PlayerHelper.UpdateData(event.getPlayer());
    }

    protected void OnRemove(StageRemovedPlayerEvent event) {
        PlayerHelper.UpdateData(event.getPlayer());
    }
}
