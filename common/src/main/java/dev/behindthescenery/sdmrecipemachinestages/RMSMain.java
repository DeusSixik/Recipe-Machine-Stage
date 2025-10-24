package dev.behindthescenery.sdmrecipemachinestages;

import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import dev.behindthescenery.sdmrecipemachinestages.compat.IRecipeUpdateListener;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import dev.behindthescenery.sdmstages.StageApi;
import dev.behindthescenery.sdmstages.data.StageContainer;
import dev.behindthescenery.sdmstages.data.StageContainerType;
import dev.behindthescenery.sdmstages.data.containers.Stage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RMSMain {
    private static final ThreadLocal<UUID> BlockOwner = new ThreadLocal<>();

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

    public static EventResult onPlaceBlock(Level level, BlockPos blockPos, BlockState blockState, @Nullable Entity entity) {
        if(entity instanceof final ServerPlayer serverPlayer) {
            final BlockEntity blockEntity = serverPlayer.serverLevel().getBlockEntity(blockPos);
            if(blockEntity != null) {
                RMSUtils.setBlockEntityOwner(blockEntity, serverPlayer);
            }
        }

        return EventResult.interruptDefault();
    }

    public static void setBlockOwner(UUID blockOwner) {
        BlockOwner.set(blockOwner);
    }

    public static UUID getBlockOwner() {
        return BlockOwner.get();
    }
}
