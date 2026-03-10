package dev.behindthescenery.sdmrecipemachinestages.api;

import dev.architectury.networking.NetworkManager;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.data.*;
import dev.behindthescenery.sdmrecipemachinestages.network.SyncRecipesAndStagesS2C;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.*;
import java.util.function.Consumer;

public class RMSApi {

    public static void register(RecipeType<?> recipeType, List<ResourceLocation> recipeIds, String stage) {
        RMSContainer.Instance.register(new RecipeBlockType(stage, recipeType, recipeIds));
        syncRecipesWithPlayers(recipeType, recipeIds, stage);
    }

    public static void addRecipe(String recipeType, ResourceLocation recipeID, String stage) {
        register(parseType(recipeType), List.of(recipeID), stage);
    }

    public static void addRecipe(String recipeType, ResourceLocation[] recipeIDs, String stage) {
        register(parseType(recipeType), List.of(recipeIDs), stage);
    }

    public static void addRecipe(RecipeType<?> recipeType, ResourceLocation recipeID, String stage) {
        register(recipeType, List.of(recipeID), stage);
    }

    public static void addRecipe(RecipeType<?> recipeType, ResourceLocation[] recipeIDs, String stage) {
        register(recipeType, List.of(recipeIDs), stage);
    }

    public static void addRecipeByMod(String recipeType, String modId, String stage) {
        addRecipeByMod(parseType(recipeType), new String[]{modId}, stage);
    }

    public static void addRecipeByMod(RecipeType<?> recipeType, String modId, String stage) {
        addRecipeByMod(recipeType, new String[]{modId}, stage);
    }

    public static void addRecipeByMod(String recipeTypeStr, String[] modIds, String stage) {
        addRecipeByMod(parseType(recipeTypeStr), modIds, stage);
    }

    public static void addRecipeByMod(RecipeType<?> recipeType, String[] modIds, String stage) {
        List<RecipeBlockType> existingData = RMSContainer.Instance.getRecipesByTypeData()
                .getOrDefault(recipeType, Collections.emptyList());

        Set<ResourceLocation> restrictedIds = new HashSet<>();
        for (int i = 0; i < existingData.size(); i++) {
            restrictedIds.addAll(existingData.get(i).recipes_id());
        }

        Set<String> targetMods = new HashSet<>(Arrays.asList(modIds));

        List<ResourceLocation> recipeIds = RMSUtils.getAllRecipesUnsafe(recipeType)
                .stream()
                .map(RecipeHolder::id)
                .filter(id -> !restrictedIds.contains(id) && (targetMods.isEmpty() || targetMods.contains(id.getNamespace())))
                .toList();

        register(recipeType, recipeIds, stage);
    }

    public static void addRecipeByMachine(String recipeType, String stage) {
        addRecipeByMod(recipeType, EMPTY, stage);
    }

    public static void addRecipeByMachine(RecipeType<?> recipeType, String stage) {
        addRecipeByMod(recipeType, EMPTY, stage);
    }

    public static void register(Class<?> blockClass, List<ItemStack> inputs, String stage, RecipeBlockClass type) {
        switch (type) {
            case Input -> RMSContainer.Instance.register(new RecipeBlockInput(blockClass, stage, inputs));
            case Out -> RMSContainer.Instance.register(new RecipeBlockOutput(blockClass, stage, inputs));
        }
    }

    public static void printAllRecipes(ResourceLocation recipeTypeId, Consumer<String> onGet) {
        printAllRecipes(BuiltInRegistries.RECIPE_TYPE.get(recipeTypeId), onGet);
    }

    public static void printAllRecipes(RecipeType<?> recipeType, Consumer<String> onGet) {
        if (RMSMain.getServer() == null || recipeType == null) return;

        onGet.accept("Start dump recipes: ");
        for (RecipeHolder<?> recipeHolder : RMSUtils.getAllRecipesUnsafe(recipeType)) {
            onGet.accept(recipeHolder.id().toString());
        }
    }

    public static void syncRecipesWithPlayers(RecipeType<?> recipeType, List<ResourceLocation> recipeIds, String stage) {
        final MinecraftServer server = RMSMain.getServer();
        if (server == null) return;

        final ResourceLocation recipeTypeId = BuiltInRegistries.RECIPE_TYPE.getKey(recipeType);
        final SyncRecipesAndStagesS2C packet = new SyncRecipesAndStagesS2C(recipeTypeId, recipeIds, stage);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NetworkManager.sendToPlayer(player, packet);
        }
    }

    public static void syncRecipeContainerWithPlayers() {
        final MinecraftServer server = RMSMain.getServer();
        if (server == null) return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            RMSContainer.Instance.sendTo(player);
        }
    }

    public static String[] getAllRecipeTypes() {
        return BuiltInRegistries.RECIPE_TYPE.keySet().stream()
                .map(ResourceLocation::toString)
                .toArray(String[]::new);
    }

    private static RecipeType<?> parseType(String recipeType) {
        ResourceLocation loc = ResourceLocation.tryParse(recipeType);
        RecipeType<?> type = loc != null ? BuiltInRegistries.RECIPE_TYPE.get(loc) : null;
        if (type == null) {
            throw new IllegalArgumentException("Can't find recipeType by id: " + recipeType);
        }
        return type;
    }

    public enum RecipeBlockClass {
        Input,
        Out
    }

    private static final String[] EMPTY = new String[0];
}
