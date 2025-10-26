package dev.behindthescenery.sdmrecipemachinestages.data;

import dev.behindthescenery.sdmrecipemachinestages.compat.info.InfoContext;
import org.jetbrains.annotations.Nullable;

public class AbstractRecipeBlock implements BaseRecipeStage{

    protected final Class<?> blockProduction;
    protected final String stageId;

    public AbstractRecipeBlock(Class<?> blockProduction, String stageId) {
        this.blockProduction = blockProduction;
        this.stageId = stageId;
    }

    @Override
    public String stageId() {
        return stageId;
    }

    @Override
    public String toString() {
        return "AbstractRecipeBlock{" +
                "blockProduction=" + blockProduction.getName() +
                ", stageId='" + stageId + '\'' +
                '}';
    }

    @Nullable
    public InfoContext getContextInfo() {
        return null;
    }
}
