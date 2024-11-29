package net.sdm.recipemachinestage.utils;


import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeTypeCategory;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeWrapper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import mezz.jei.api.recipe.RecipeType;
import net.minecraftforge.fml.ModList;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Source
 * https://github.com/jaredlll08/RecipeStages/blob/1.20.1/src/main/java/com/blamejared/recipestages/RecipeStagesUtil.java#L14
 */
public class RecipeStagesUtil {

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {

        return (T) o;
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
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }
}
