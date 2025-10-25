package dev.behindthescenery.sdmrecipemachinestages.neoforge.utils;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingInput;
import com.simibubi.create.content.kinetics.crafter.RecipeGridHandler;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockEntityCustomData;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockOwnerData;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.CustomData;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.create.FanProcessingTypePatch;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RMSCreateUtils {

    public static <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> checkRecipeAndReturn(BlockEntity entity, AllRecipeTypes recipeTypes, I input, Level world) {
        final Optional<RecipeHolder<R>> holderOptional = recipeTypes.find(input, world);
        if(holderOptional.isEmpty()) return holderOptional;

        final RecipeHolder<R> holder = holderOptional.get();
        if(!RMSUtils.canProcess(entity, holder)) return Optional.empty();

        return holderOptional;
    }

    public static ItemStack tryToApplyRecipe(Level world, RecipeGridHandler.GroupedItems items, BlockEntity entity) {
        final CustomData customData = BlockEntityCustomData.getDataOrThrow(entity);
        return tryToApplyRecipe(world, items, (UUID) customData.getData(BlockOwnerData.OWNER_KEY));
    }

    public static ItemStack tryToApplyRecipe(Level world, RecipeGridHandler.GroupedItems items, UUID playerId) {
        items.calcStats();
        final CraftingInput craftingInput = MechanicalCraftingInput.of(items);
        final RegistryAccess registryAccess = world.registryAccess();

        ItemStack result = null;
        if (AllConfigs.server().recipes.allowRegularCraftingInCrafter.get()) {
            final Optional<RecipeHolder<CraftingRecipe>> findRecipe = world.getRecipeManager()
                    .getRecipeFor(RecipeType.CRAFTING, craftingInput, world)
                    .filter(r -> RecipeGridHandler.isRecipeAllowed(r, craftingInput));

            if(findRecipe.isPresent()) {
                final RecipeHolder<CraftingRecipe> recipeHolder = findRecipe.get();

                if(RMSUtils.canProcess(playerId, recipeHolder))
                    result = recipeHolder.value().assemble(craftingInput, registryAccess);
            }
        }

        if (result == null) {
            final Optional<RecipeHolder<Recipe<CraftingInput>>> findRecipe =
                    AllRecipeTypes.MECHANICAL_CRAFTING.find(craftingInput, world);

            if(findRecipe.isPresent()) {
                final RecipeHolder<Recipe<CraftingInput>> recipeHolder = findRecipe.get();

                if(RMSUtils.canProcess(playerId, recipeHolder))
                    result = recipeHolder.value().assemble(craftingInput, registryAccess);
            }
        }

        return result;
    }

    public static boolean canItemBeFilled(Level world, ItemStack stack, UUID ownerId) {
        final SingleRecipeInput input = new SingleRecipeInput(stack);

        Optional<RecipeHolder<FillingRecipe>> assemblyRecipe =
                SequencedAssemblyRecipe.getRecipe(world, input, AllRecipeTypes.FILLING.getType(), FillingRecipe.class);
        if (assemblyRecipe.isEmpty())
            assemblyRecipe = AllRecipeTypes.FILLING.find(input, world);


        return assemblyRecipe.map(fillingRecipeRecipeHolder -> RMSUtils.canProcess(ownerId, fillingRecipeRecipeHolder))
                .orElseGet(() -> GenericItemFilling.canItemBeFilled(world, stack));

    }

    public static boolean canItemBeEmptied(Level world, ItemStack stack, UUID ownerId) {
        if (PotionFluidHandler.isPotionItem(stack))
            return true;

        final Optional<RecipeHolder<Recipe<SingleRecipeInput>>> find = AllRecipeTypes.EMPTYING.find(new SingleRecipeInput(stack), world);

        if (find.isPresent()) {
            return RMSUtils.canProcess(ownerId, find.get());
        }

        IFluidHandlerItem capability = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability == null)
            return false;
        for (int i = 0; i < capability.getTanks(); i++) {
            if (capability.getFluidInTank(i)
                    .getAmount() > 0)
                return true;
        }
        return false;
    }

    public static boolean applyProcessing(ItemEntity entity, FanProcessingType type, UUID ownerID) {
        if (decrementProcessingTime(entity, type) != 0)
            return false;

        List<ItemStack> stacks = FanProcessingTypePatch.getPatch(type).bts$process(entity.getItem(), entity.level(), ownerID);
        if (stacks == null)
            return false;
        if (stacks.isEmpty()) {
            entity.discard();
            return false;
        }
        entity.setItem(stacks.remove(0));
        for (ItemStack additional : stacks) {
            ItemEntity entityIn = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), additional);
            entityIn.setDeltaMovement(entity.getDeltaMovement());
            entity.level().addFreshEntity(entityIn);
        }
        return true;
    }

    private static int decrementProcessingTime(ItemEntity entity, FanProcessingType type) {
        CompoundTag nbt = entity.getPersistentData();

        if (!nbt.contains("CreateData"))
            nbt.put("CreateData", new CompoundTag());
        CompoundTag createData = nbt.getCompound("CreateData");

        if (!createData.contains("Processing"))
            createData.put("Processing", new CompoundTag());
        CompoundTag processing = createData.getCompound("Processing");

        if (!processing.contains("Type") || AllFanProcessingTypes.parseLegacy(processing.getString("Type")) != type) {
            ResourceLocation key = CreateBuiltInRegistries.FAN_PROCESSING_TYPE.getKey(type);
            if (key == null)
                throw new IllegalArgumentException("Could not get id for FanProcessingType " + type + "!");

            processing.putString("Type", key.toString());
            int timeModifierForStackSize = ((entity.getItem()
                    .getCount() - 1) / 16) + 1;
            int processingTime =
                    (AllConfigs.server().kinetics.fanProcessingTime.get() * timeModifierForStackSize) + 1;
            processing.putInt("Time", processingTime);
        }

        int value = processing.getInt("Time") - 1;
        processing.putInt("Time", value);
        return value;
    }

    public static boolean canPolish(Level world, ItemStack stack, UUID ownerId) {
        return !getMatchingRecipes(world, stack, ownerId).isEmpty();
    }


    public static ItemStack applyPolish(Level world, Vec3 position, ItemStack stack, ItemStack sandPaperStack, UUID ownerId) {
        List<RecipeHolder<Recipe<SingleRecipeInput>>> matchingRecipes = getMatchingRecipes(world, stack, ownerId);
        if(matchingRecipes.isEmpty()) return stack;
        return matchingRecipes.getFirst().value().assemble(new SingleRecipeInput(stack), world.registryAccess()).copy();
    }

    public static List<RecipeHolder<Recipe<SingleRecipeInput>>> getMatchingRecipes(Level world, ItemStack stack, UUID ownerId) {
        return RMSUtils.filterRecipes(world.getRecipeManager()
                .getRecipesFor(AllRecipeTypes.SANDPAPER_POLISHING.getType(), new SingleRecipeInput(stack), world), ownerId);
    }
}
