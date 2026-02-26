package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.mi;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(value = CrafterComponent.class, remap = false)
public class MixinCrafterComponent {

    @Shadow
    @Final
    private MachineProcessCondition.Context conditionContext;

    @Shadow
    @Final
    private CrafterComponent.Behavior behavior;

    @Shadow
    @Final
    private CrafterComponent.Inventory inventory;

    @Shadow
    private RecipeHolder<MachineRecipe> activeRecipe;

    @Unique
    private long bts$nextCheckTick = 0;
    @Unique
    private ResourceLocation bts$lastRecipeId = null;
    @Unique
    private boolean bts$wasAllowed = false;

    @Inject(method = "getRecipes()Ljava/lang/Iterable;", at = @At(value = "INVOKE", target = "Laztech/modern_industrialization/machines/components/CrafterComponent;getRecipes(Lnet/minecraft/server/level/ServerLevel;Laztech/modern_industrialization/machines/recipe/MachineRecipeType;Ljava/util/List;)Ljava/util/List;"), cancellable = true)
    private void bts$getRecipes(CallbackInfoReturnable<Iterable<RecipeHolder<MachineRecipe>>> cir) {
        cir.setReturnValue(bts$customGetRecipes(this.behavior.getCrafterWorld(), this.behavior.recipeType(), this.inventory.getItemInputs()));
    }

    @Inject(method = "loadDelayedActiveRecipe", at = @At(value = "INVOKE", target = "Laztech/modern_industrialization/machines/recipe/MachineRecipeType;getRecipe(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/item/crafting/RecipeHolder;", shift = At.Shift.AFTER))
    private void bts$loadDelayedActiveRecipe(CallbackInfo ci) {
        final RecipeHolder<MachineRecipe> current = this.activeRecipe;

        if (current == null) return;

        final long gameTime = this.behavior.getCrafterWorld().getGameTime();
        final ResourceLocation currentId = current.id();

        if (currentId.equals(bts$lastRecipeId) && gameTime < bts$nextCheckTick) {

            if (!bts$wasAllowed)
                this.activeRecipe = null;

            return;
        }

        bts$wasAllowed = RMSUtils.canProcess(RMSUtils.getBlockOwner(conditionContext.getBlockEntity()), current);
        bts$lastRecipeId = currentId;
        bts$nextCheckTick = gameTime + 20;

        if (!bts$wasAllowed)
            this.activeRecipe = null;

    }

    @Unique
    private List<RecipeHolder<MachineRecipe>> bts$customGetRecipes(ServerLevel level, MachineRecipeType recipeType, List<ConfigurableItemStack> itemInputs) {
        UUID owner = RMSUtils.getBlockOwner(conditionContext.getBlockEntity());
        List<RecipeHolder<MachineRecipe>> result = new ArrayList<>(RMSUtils.filterRecipes(recipeType.getFluidOnlyRecipes(level), owner));

        Set<Item> processedItems = new HashSet<>();
        for (int i = 0; i < itemInputs.size(); i++) {
            ConfigurableItemStack stack = itemInputs.get(i);
            Item item = stack.getResource().getItem();
            if (!stack.isEmpty() && processedItems.add(item)) {
                result.addAll(RMSUtils.filterRecipes(recipeType.getMatchingRecipes(level, item), owner));
            }
        }
        return result;
    }
}
