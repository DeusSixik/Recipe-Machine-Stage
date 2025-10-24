package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.applied;

import appeng.recipes.transform.TransformCircumstance;
import appeng.recipes.transform.TransformLogic;
import appeng.recipes.transform.TransformRecipe;
import appeng.recipes.transform.TransformRecipeInput;
import com.google.common.collect.Lists;
import dev.behindthescenery.sdmrecipemachinestages.SdmRecipeMachineStages;
import dev.behindthescenery.sdmrecipemachinestages.data.RMSContainer;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

@Mixin(TransformLogic.class)
public class TransformLogicMixin {

    /**
     * @author Sixik
     * @reason Optimize and
     */
    @Overwrite
    @SuppressWarnings("removal")
    public static boolean tryTransform(ItemEntity entity, Predicate<TransformCircumstance> circumstancePredicate) {
        final Level level = entity.level();

        if (level.isClientSide()) return false;

        final BlockPos pos = entity.blockPosition();
        final double x = pos.getX();
        final double y = pos.getY();
        final double z = pos.getZ();
        final AABB region = new AABB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
        final List<ItemEntity> itemEntities = new ArrayList<>();
        for (final Entity e : level.getEntities(null, region)) {
            if (e instanceof ItemEntity ie && !ie.isRemoved() && !ie.getItem().isEmpty()) {
                itemEntities.add(ie);
            }
        }


        List<RecipeHolder<TransformRecipe>> recipeHolders = level.getRecipeManager().getAllRecipesFor(TransformRecipe.TYPE);

        if(RMSUtils.hasRestrictionsForType(TransformRecipe.TYPE)) {
            final Player player = RMSUtils.getNearestPlayer(entity.level(), pos);
            if(player == null) return false;
            recipeHolders = RMSUtils.filterRecipes(recipeHolders, player);
        }

        for (final RecipeHolder<TransformRecipe> holder : recipeHolders) {
            final TransformRecipe recipe = holder.value();

            if (!circumstancePredicate.test(recipe.circumstance) || recipe.ingredients.isEmpty())
                continue;

            final List<Ingredient> missingIngredients = new ArrayList<>(recipe.ingredients);
            final Reference2IntMap<ItemEntity> consumed = new Reference2IntOpenHashMap<>(missingIngredients.size());

            if (recipe.circumstance.isExplosion()) {
                boolean match = false;
                for (Ingredient i : missingIngredients) {
                    if (i.test(entity.getItem())) {
                        match = true;
                        break;
                    }
                }
                if (!match) continue;
            } else if (!missingIngredients.get(0).test(entity.getItem())) {
                continue;
            }

            for (final ItemEntity ie : itemEntities) {
                final ItemStack stack = ie.getItem();
                if (stack.isEmpty()) continue;

                for (int i = 0; i < missingIngredients.size(); i++) {
                    final Ingredient ing = missingIngredients.get(i);
                    final int claimed = consumed.getInt(ie);

                    if (ing.test(stack) && stack.getCount() > claimed) {
                        consumed.put(ie, claimed + 1);
                        missingIngredients.remove(i);
                        break;
                    }
                }

                if (missingIngredients.isEmpty()) break;
            }

            if (missingIngredients.isEmpty()) {
                final List<ItemStack> consumedStacks = new ArrayList<>(consumed.size());
                for (Reference2IntMap.Entry<ItemEntity> e : consumed.reference2IntEntrySet()) {
                    final ItemEntity ie = e.getKey();
                    final int amount = e.getIntValue();
                    consumedStacks.add(ie.getItem().split(amount));
                    if (ie.getItem().isEmpty()) ie.discard();
                }

                final TransformRecipeInput input = new TransformRecipeInput(consumedStacks);
                final ItemStack result = recipe.assemble(input, level.registryAccess());
                final RandomSource rand = level.random;

                final double spawnX = Math.floor(x) + 0.25 + rand.nextDouble() * 0.5;
                final double spawnY = Math.floor(y) + 0.25 + rand.nextDouble() * 0.5;
                final double spawnZ = Math.floor(z) + 0.25 + rand.nextDouble() * 0.5;
                final double velX = rand.nextDouble() * 0.25 - 0.125;
                final double velY = rand.nextDouble() * 0.25 - 0.125;
                final double velZ = rand.nextDouble() * 0.25 - 0.125;

                final ItemEntity output = new ItemEntity(level, spawnX, spawnY, spawnZ, result);
                output.setDeltaMovement(velX, velY, velZ);
                level.addFreshEntity(output);
                return true;
            }
        }

        return false;
    }
}
