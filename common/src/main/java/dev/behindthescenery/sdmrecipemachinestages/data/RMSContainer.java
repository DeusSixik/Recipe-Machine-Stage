package dev.behindthescenery.sdmrecipemachinestages.data;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.behindthescenery.sdmrecipemachinestages.RMSApi;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.compat.IRecipeUpdateListener;
import dev.behindthescenery.sdmrecipemachinestages.compat.RMSIntegrations;
import dev.behindthescenery.sdmrecipemachinestages.compat.kubejs.events.RMSJSEvents;
import dev.behindthescenery.sdmrecipemachinestages.compat.kubejs.events.RMSStageKubeEvent;
import dev.behindthescenery.sdmrecipemachinestages.network.SyncRMSContainerDataS2C;
import dev.behindthescenery.sdmrecipemachinestages.supported.RMSSupportedTypes;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class RMSContainer extends SimplePreparableReloadListener<Void> {

    public boolean isServer = false;

    public static final RMSContainer Instance = new RMSContainer();

    protected Object2ObjectMap<RecipeType<?>, List<RecipeBlockType>> RecipeStagesByTypeData = new Object2ObjectArrayMap<>();
    protected Object2ObjectMap<Class<?>, List<AbstractRecipeBlock>> RecipeStagesByBlockData = new Object2ObjectArrayMap<>();

    protected volatile boolean reloading = true;

    public void register(RecipeBlockType type) {
        registerImpl(type);
    }

    public void register(AbstractRecipeBlock block) {
        registerImpl(block);
    }

    protected final void registerImpl(RecipeBlockType type) {
        RMSSupportedTypes.isSupported(type.recipeType());
        RecipeStagesByTypeData.computeIfAbsent(type.recipeType(), s -> new ArrayList<>()).add(type);
        RMSMain.LOGGER.info("Register new restriction " + type.toString());
    }

    protected final void registerImpl(AbstractRecipeBlock block) {
        RMSSupportedTypes.isSupported(block.blockProduction);
        RecipeStagesByBlockData.computeIfAbsent(block.blockProduction, s -> new ArrayList<>()).add(block);
        RMSMain.LOGGER.info("Register new restriction " + block.toString());
    }

    public Optional<RecipeBlockType> getRecipeBlockByType(RecipeType<?> recipeType, ResourceLocation recipeId) {
        return RecipeStagesByTypeData.getOrDefault(recipeType, new ArrayList<>())
                .stream().filter(s -> s.contains(recipeId)).findFirst();
    }

    public Map<RecipeType<?>, List<RecipeBlockType>> getRecipesByTypeData() {
        return RecipeStagesByTypeData;
    }

    public Object2ObjectMap<Class<?>, List<AbstractRecipeBlock>> getRecipesByBlockData() {
        return RecipeStagesByBlockData;
    }

    public List<RecipeBlockType> getRecipesBlockByType(RecipeType<?> recipeType) {
        return RecipeStagesByTypeData.getOrDefault(recipeType, new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractRecipeBlock> List<T> getRecipesBlockByInput(Class<?> blockId, RMSApi.RecipeBlockClass type) {
        switch (type) {
            case Input -> {
               return RecipeStagesByBlockData.getOrDefault(blockId, new ArrayList<>()).stream().filter(s -> s instanceof RecipeBlockInput).map(s -> (T)s).toList();
            }
            case Out -> {
                return RecipeStagesByBlockData.getOrDefault(blockId, new ArrayList<>()).stream().filter(s -> s instanceof RecipeBlockOutput).map(s -> (T)s).toList();
            }
        }

        throw new UnsupportedOperationException("Can't find recipe by type: " + type.name());
    }

    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        clearData();
        return null;
    }

    public void clearData() {
        RecipeStagesByTypeData.clear();
        RecipeStagesByBlockData.clear();
    }

    public void startReloading(MinecraftServer server) {
        reloading = true;
        isServer = true;

        clearData();

        RMSIntegrations.kubeJSAddRecipes.run();
    }

    public void endReloading(MinecraftServer server) {
        RMSMain.getListeners().forEach(IRecipeUpdateListener::updateRecipe);
        reloading = false;
    }

    @Override
    protected void apply(Void object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
    }

    // Not optimized.
    // TODO: Make batching packets
    public void sendTo(ServerPlayer player) {
        for (List<RecipeBlockType> value : RecipeStagesByTypeData.values()) {
            for (RecipeBlockType recipeBlockType : value) {

                if(recipeBlockType == null) continue;

                NetworkManager.sendToPlayer(player, new SyncRMSContainerDataS2C(recipeBlockType));
            }
        }
    }

    public boolean isReloading() {
        return reloading;
    }

}
