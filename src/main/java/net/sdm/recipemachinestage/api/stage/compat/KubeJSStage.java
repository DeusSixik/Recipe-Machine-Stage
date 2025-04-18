package net.sdm.recipemachinestage.compat.kubejs;

import net.sdm.recipemachinestage.api.stage.IStage;

import java.util.List;

public class KubeJSStage implements IStage {
    
    public KubeJSStage() {
        
    }
    
    @Override
    public List<String> getStages() {
        return ;
    }

    @Override
    public boolean hasStage(String stage) {
        return false;
    }
}
