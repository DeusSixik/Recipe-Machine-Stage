package dev.behindthescenery.sdmrecipemachinestages.compat;

import dev.architectury.event.EventResult;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;


public class RMSCommonReiPlugin implements REIClientPlugin, IRecipeUpdateListener {

    public RMSCommonReiPlugin() {
        RMSMain.addListener(this);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerVisibilityPredicate((category, display) -> EventResult.interruptTrue());
    }

    @Override
    public void updateRecipe() {

    }
}
