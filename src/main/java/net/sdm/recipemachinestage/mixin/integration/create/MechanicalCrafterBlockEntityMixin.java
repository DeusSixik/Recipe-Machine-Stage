package net.sdm.recipemachinestage.mixin.integration.create;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingInventory;
import com.simibubi.create.content.kinetics.crafter.RecipeGridHandler;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.Optional;

@Mixin(value = MechanicalCrafterBlockEntity.class)
public class MechanicalCrafterBlockEntityMixin {

    private MechanicalCrafterBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Unique
    private boolean recipe_machine_stage$isRecipe(CraftingRecipe craftingRecipe, CraftingContainer craftinginventory) {
        Optional<Recipe<CraftingContainer>> recipe = AllRecipeTypes.MECHANICAL_CRAFTING.find(craftinginventory, world);

        if(StageContainer.hasRecipes(craftingRecipe.getType()) && d1.isPresent() && Objects.requireNonNull(thisEntity.getLevel()).getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.get().getType(), recipe.get().getId());
            if (recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                if (player != null) {
                    if (!player.hasStage(recipeBlockType.stage)) {
                        return false;
                    }
                }
            }
        }

        return RecipeGridHandler.isRecipeAllowed(craftingRecipe, craftinginventory);
    }

    private Level world;
    private RegistryAccess registryAccess;
    private Optional<IOwnerBlock> d1;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/crafter/RecipeGridHandler;tryToApplyRecipe(Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/kinetics/crafter/RecipeGridHandler$GroupedItems;)Lnet/minecraft/world/item/ItemStack;"), remap = false)
    public ItemStack sdm$tick(Level world, RecipeGridHandler.GroupedItems items) {
        this.d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();

        items.calcStats();
        CraftingContainer craftinginventory = new MechanicalCraftingInventory(items);
        ItemStack result = null;
        this.world = world;
        this.registryAccess = world.registryAccess();
        if (AllConfigs.server().recipes.allowRegularCraftingInCrafter.get()) {
            result = world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftinginventory, world)
                    .filter((r) -> recipe_machine_stage$isRecipe(r, craftinginventory))
                    .map((r) -> r.assemble(craftinginventory, registryAccess))
                    .orElse(null);
        }

        if (result == null) {
            Optional<Recipe<CraftingContainer>> recipe = AllRecipeTypes.MECHANICAL_CRAFTING.find(craftinginventory, world);
            if(recipe.isPresent()) {

                if (StageContainer.hasRecipes(AllRecipeTypes.MECHANICAL_CRAFTING.getType()) && d1.isPresent() && thisEntity.getLevel().getServer() != null) {
                    IOwnerBlock ownerBlock = d1.get();
                    RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.get().getType(), recipe.get().getId());
                    if (recipeBlockType != null) {
                        PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                        if (player != null) {
                            if (!player.hasStage(recipeBlockType.stage)) {
                                return null;
                            }
                        }
                    }
                }
                result = recipe.map((r) -> r.assemble(craftinginventory, registryAccess)).orElse((ItemStack) null);
            }
        }

        return result;
    }
}
