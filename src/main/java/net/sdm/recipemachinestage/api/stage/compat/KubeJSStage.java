package net.sdm.recipemachinestage.api.stage.compat;

import dev.latvian.mods.kubejs.integration.forge.gamestages.GameStagesWrapper;
import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.recipemachinestage.api.stage.IStage;
import net.sdm.recipemachinestage.utils.PlayerHelper;

import java.util.Collection;

public class KubeJSStage implements IStage {
    
    public KubeJSStage() {}
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public Collection<String> getStages() {
        return GameStagesWrapper.get(Minecraft.getInstance().player).getAll();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasStage(String stage) {
        return GameStagesWrapper.get(Minecraft.getInstance().player).has(stage);
    }

    @Override
    public String getModID() {
        return "kubejs";
    }

    @Override
    public void registerEventsServer() {
        Stages.added((stageChangeEvent -> PlayerHelper.UpdateData(stageChangeEvent.getPlayer())));
        Stages.removed((stageChangeEvent -> PlayerHelper.UpdateData(stageChangeEvent.getPlayer())));
    }
}
