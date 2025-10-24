package net.sdm.recipemachinestage.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.fml.ModList;
import net.sdm.recipemachinestage.compat.jei.JEIPlugin;

public class ClientEventHandler {


    public static void recipes(RecipesUpdatedEvent event) {
        if(ModList.get().isLoaded("jei")) {
            Minecraft.getInstance().execute(JEIPlugin::sync);
        }
    }
}
