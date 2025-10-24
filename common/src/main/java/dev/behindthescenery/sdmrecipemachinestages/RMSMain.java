package dev.behindthescenery.sdmrecipemachinestages;

import com.mojang.logging.LogUtils;
import dev.behindthescenery.sdmrecipemachinestages.compat.IRecipeUpdateListener;
import dev.behindthescenery.sdmstages.StageApi;
import dev.behindthescenery.sdmstages.data.StageContainer;
import dev.behindthescenery.sdmstages.data.StageContainerType;
import dev.behindthescenery.sdmstages.data.containers.Stage;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RMSMain {
    public static final Logger LOGGER = LogUtils.getLogger();

    private static List<IRecipeUpdateListener> listeners = new ArrayList<>();

    private static MinecraftServer currentServer;
    private static boolean isGlobal;
    private static StageContainer ServerContainer;
    private static RegistryAccess.Frozen registryAccess;
    private static RecipeManager recipeManager;

    public static void onServerStarted(MinecraftServer server) {
        onServerReloadResources(server);
        ServerContainer = StageApi.getServerStage();
        isGlobal = ServerContainer.getContainerType() == StageContainerType.GLOBAL;
    }

    public static void onServerReloadResources(MinecraftServer server) {
        currentServer = server;
        registryAccess = currentServer.registryAccess();
        recipeManager = currentServer.getRecipeManager();
    }

    public static MinecraftServer getServer() {
        return currentServer;
    }

    public static StageContainer getStageContainer() {
        return ServerContainer;
    }

    public static boolean isGlobal() {
        return isGlobal;
    }

    public static RegistryAccess.Frozen getRegistryAccess() {
        return registryAccess;
    }

    public static RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public static List<IRecipeUpdateListener> getListeners() {
        return listeners.stream().toList();
    }

    public static void addListener(IRecipeUpdateListener listener) {
        if(listeners.contains(listener)) return;

        listeners.add(listener);
        LOGGER.info("Register Recipe Listener: {}", listener.getClass().getName());
    }

    public static void onStageSync(Stage stage, StageContainer stageContainer) {
        getListeners().forEach(IRecipeUpdateListener::updateRecipe);
    }
}
