package net.sdm.recipemachinestage.utils;


import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeTypeCategory;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeWrapper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipeStagesUtil {

    public static <T extends Recipe<?>> Optional<T> checkRecipe(Optional<T> recipeOptional, BlockEntity entity) {
        return recipeOptional.map(t -> checkRecipe(t, entity));
    }

    public static <T extends Recipe<?>> Optional<T> checkRecipeOptional(T recipe, BlockEntity entity) {
        return Optional.ofNullable(checkRecipe(recipe, entity));
    }

    public static <T extends Recipe<?>> void checkRecipe(T recipe, BlockEntity entity, CallbackInfo ci) {
        if(checkRecipe(recipe, entity) == null) ci.cancel();
    }

    public static @Nullable <T extends Recipe<?>> T checkRecipe(T recipe, BlockEntity entity) {
        if(recipe == null) return null;

        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(recipe.getType())) return recipe;

        if (entity == null) {
            return recipe;
        }

        Optional<IOwnerBlock> optionalOwnerBlock = entity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (optionalOwnerBlock.isPresent() && entity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = optionalOwnerBlock.get();

            PlayerHelper.RMSStagePlayerData playerData = PlayerHelper.getPlayerByGameProfile(entity.getLevel().getServer(), ownerBlock.getOwner());
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());

            if(recipeBlockType == null) return recipe;
            if(playerData == null) return recipe;

            if (!playerData.hasStage(recipeBlockType.stage)) {
                return null;
            }

        }

        return recipe;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {

        return (T) o;
    }

    public static boolean canRecipeOnBlockEntity(BlockEntity block, Recipe<?> recipe) {
        if(recipe == null) return true;


        Optional<IOwnerBlock> optionalOwnerBlock = block.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (optionalOwnerBlock.isPresent() && block.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = optionalOwnerBlock.get();

            PlayerHelper.RMSStagePlayerData playerData = PlayerHelper.getPlayerByGameProfile(block.getLevel().getServer(), ownerBlock.getOwner());
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());

            if(recipeBlockType == null) return true;
            if(playerData == null) return true;

            if (!playerData.hasStage(recipeBlockType.stage)) {
                return false;
            }

        }

        return true;
    }


    public static<T> RecipeType<T> getRecipeType(Object recipeType, IRecipeCategory<?> category) {
        if(ModList.get().isLoaded("gtceu")) {
            if (recipeType instanceof GTRecipe recipe) {
                return (RecipeType<T>) GTRecipeTypeCategory.TYPES.apply(recipe.recipeType);
            }
        }
        return (RecipeType<T>) category.getRecipeType();
    }

    public static Object getRecipe(Object recipe) {
        if(ModList.get().isLoaded("gtceu")) {
            if(recipe instanceof GTRecipe gtRecipe) {
                return new GTRecipeWrapper(gtRecipe);
            }
        }

        return  (Recipe<?>)cast(recipe);
    }

    public static boolean recipeFromMode(Recipe<?> recipe) {
        List<String> list = List.of("gtceu" /*, "extendedcrafting" */);

        return list.contains(recipe.getId().getNamespace());
    }

    public static boolean isCorrectRecipeClass(IRecipeCategory<?> category, Recipe<?> recipe) {

        boolean value = recipeFromMode(recipe);

//        System.out.println(value + " : " + recipe.getId().toString() + " : " + category.getRecipeType().getUid().toString());

        if(value) {
            if (ModList.get().isLoaded("gtceu")) {
                if (recipe instanceof GTRecipe gtRecipe && category instanceof GTRecipeTypeCategory hCategory) {
                    GTRecipeWrapper wrapper = new GTRecipeWrapper(gtRecipe);
                    return ReflectionHelper.canCast(hCategory.getRecipeType().getRecipeClass(), wrapper.getClass());
                }
            }
//            if (ModList.get().isLoaded("extendedcrafting")) {
//                if (recipe instanceof ITableRecipe tableRecipe) {
//
//                    int tier = tableRecipe.getTier();
//
//                    if(category.getRecipeType() == UltimateTableCategory.RECIPE_TYPE) {
//
//                    }
//
//                    if (tier >= 4) {
//                        if (category.getRecipeType() == UltimateTableCategory.RECIPE_TYPE) {
//                            return true;
//                        }
//                    }
//                    if (tier >= 3) {
//                        if (category.getRecipeType() == EliteTableCategory.RECIPE_TYPE) {
//                            return true;
//                        }
//                    }
//                    if (tier >= 2) {
//                        if (category.getRecipeType() == AdvancedTableCategory.RECIPE_TYPE) {
//                            return true;
//                        }
//                    }
//                    if (tier >= 1) {
//                        if (category.getRecipeType() == BasicTableCategory.RECIPE_TYPE) {
//                            return true;
//                        }
//                    }
//                }
//            }
        }

        return ReflectionHelper.canCast(category.getRecipeType().getRecipeClass(), recipe.getClass());
    }

    public static void debugMessage(Player player, Object message) {
        String isC = player.isLocalPlayer() ? "CLIENT" : "SERVER";
        System.out.println("[" + isC + "]: " + message.toString());
    }


    public static int[] encodeStringToInts(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        int[] ints = new int[(bytes.length + 3) / 4];
        for (int i = 0; i < bytes.length; i++) {
            ints[i / 4] |= (bytes[i] & 0xFF) << ((i % 4) * 8);
        }
        return ints;
    }

    public static String decodeIntsToString(int[] ints) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i : ints) {
            for (int j = 0; j < 4; j++) {
                byte b = (byte) ((i >> (j * 8)) & 0xFF);
                if (b != 0) {
                    outputStream.write(b);
                }
            }
        }
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    public static <T extends Recipe<?>> List<T> checkRecipe(List<T> returnValue, BlockEntity blockEntity) {
        List<T> recipes = new ArrayList<>();
        for (T recipe : returnValue) {
            if(canRecipeOnBlockEntity(blockEntity, recipe)) {
                recipes.add(recipe);
            }
        }

        return recipes;
    }
}
