package net.sdm.recipemachinestage.utils;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class CreateUtilsHelper {

    public static class CustomSequenceAssembly {

//        public static <R extends ProcessingRecipe<?>> Optional<R> getRecipe(Level world, ItemStack item, RecipeType<R> type, Class<R> recipeClass) {
//            List<SequencedAssemblyRecipe> all = world.getRecipeManager().getAllRecipesFor(AllRecipeTypes.SEQUENCED_ASSEMBLY.getType());
//
//            for (SequencedAssemblyRecipe sequencedAssemblyRecipe : all) {
//                if (appliesTo(sequencedAssemblyRecipe, item)) {
//                    SequencedRecipe<?> nextRecipe = getNextRecipe(sequencedAssemblyRecipe, item);
//                    ProcessingRecipe<?> recipe = nextRecipe.getRecipe();
//                    if (recipe.getType() == type && recipeClass.isInstance(recipe)) {
//
//                        recipe.enforceNextResult(() -> advance(sequencedAssemblyRecipe, item));
//
//                        return (Optional<R>) Optional.of((ProcessingRecipe) recipeClass.cast(recipe));
//                    }
//                }
//            }
//
//            return Optional.empty();
//        }
//
//        private static SequencedRecipe<?> getNextRecipe(SequencedAssemblyRecipe sequencedAssemblyRecipe, ItemStack input) {
//            return (SequencedRecipe)sequencedAssemblyRecipe.getSequence().get(getStep(input) % sequencedAssemblyRecipe.getSequence().size());
//        }
//
//
//        private static int getStep(ItemStack input) {
//            if (!input.hasTag()) {
//                return 0;
//            } else {
//                CompoundTag tag = input.getTag();
//                if (!tag.contains("SequencedAssembly")) {
//                    return 0;
//                } else {
//                    int step = tag.getCompound("SequencedAssembly").getInt("Step");
//                    return step;
//                }
//            }
//        }
//
//
//        private static boolean appliesTo(SequencedAssemblyRecipe sequencedAssemblyRecipe, ItemStack input) {
//            if (sequencedAssemblyRecipe.getIngredient().test(input)) {
//                return true;
//            } else {
//                return input.hasTag() && sequencedAssemblyRecipe.getTransitionalItem().getItem() == input.getItem() && input.getTag().contains("SequencedAssembly") && input.getTag().getCompound("SequencedAssembly").getString("id").equals(sequencedAssemblyRecipe.getId().toString());
//            }
//        }
//
//
//        private static ItemStack advance(SequencedAssemblyRecipe sequencedAssemblyRecipe, ItemStack input) {
//            int step = getStep(input);
//            if ((step + 1) / sequencedAssemblyRecipe.getSequence().size() >= sequencedAssemblyRecipe.getLoops()) {
//                return rollResult(sequencedAssemblyRecipe);
//            } else {
//                ItemStack advancedItem = ItemHandlerHelper.copyStackWithSize(sequencedAssemblyRecipe.getTransitionalItem(), 1);
//                CompoundTag itemTag = advancedItem.getOrCreateTag();
//                CompoundTag tag = new CompoundTag();
//                tag.putString("id", sequencedAssemblyRecipe.getId().toString());
//                tag.putInt("Step", step + 1);
//                tag.putFloat("Progress", ((float)step + 1.0F) / (float)(sequencedAssemblyRecipe.getSequence().size() * sequencedAssemblyRecipe.getLoops()));
//                itemTag.put("SequencedAssembly", tag);
//                advancedItem.setTag(itemTag);
//                return advancedItem;
//            }
//        }
//
//
//        private static ItemStack rollResult(SequencedAssemblyRecipe sequencedAssemblyRecipe) {
//            float totalWeight = 0.0F;
//
//            ProcessingOutput entry;
//            for(Iterator var2 = sequencedAssemblyRecipe.resultPool.iterator(); var2.hasNext(); totalWeight += entry.getChance()) {
//                entry = (ProcessingOutput)var2.next();
//            }
//
//            float number = Create.RANDOM.nextFloat() * totalWeight;
//            Iterator var6 = sequencedAssemblyRecipe.resultPool.iterator();
//
//            do {
//                if (!var6.hasNext()) {
//                    return ItemStack.EMPTY;
//                }
//
//                entry = (ProcessingOutput)var6.next();
//                number -= entry.getChance();
//            } while(!(number < 0.0F));
//
//            return entry.getStack().copy();
//        }

    }
}
