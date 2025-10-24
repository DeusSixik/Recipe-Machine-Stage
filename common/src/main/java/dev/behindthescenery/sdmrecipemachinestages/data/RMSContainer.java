package dev.behindthescenery.sdmrecipemachinestages.data;

import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.supported.RMSSupportedTypes;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RMSContainer extends SimplePreparableReloadListener<Void> {

    public static final RMSContainer Instance = new RMSContainer();

    protected Object2ObjectMap<RecipeType<?>, List<RecipeBlockType>> RecipeStagesData = new Object2ObjectArrayMap<>();

    public void register(RecipeBlockType type) {
        RMSSupportedTypes.isSupported(type.recipeType());
        RecipeStagesData.computeIfAbsent(type.recipeType(), s -> new ArrayList<>()).add(type);
        RMSMain.LOGGER.info("Register new restriction " + type.toString());
    }

    public Optional<RecipeBlockType> getRecipeBlock(RecipeType<?> recipeType, ResourceLocation recipeId) {
        return RecipeStagesData.getOrDefault(recipeType, new ArrayList<>())
                .stream().filter(s -> s.contains(recipeId)).findFirst();
    }

    public Map<RecipeType<?>, List<RecipeBlockType>> getRecipesData() {
        return RecipeStagesData;
    }

    public List<RecipeBlockType> getRecipesBlock(RecipeType<?> recipeType) {
        return RecipeStagesData.getOrDefault(recipeType, new ArrayList<>());
    }

    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        RecipeStagesData.clear();
        return null;
    }

    @Override
    protected void apply(Void object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {

    }
}
