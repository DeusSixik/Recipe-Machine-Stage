package net.sdm.recipemachinestage.mixin.integration.create;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingInventory;
import com.simibubi.create.content.kinetics.crafter.RecipeGridHandler;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(value = MechanicalCrafterBlockEntity.class, remap = false)
public class MechanicalCrafterBlockEntityMixin {

    @Shadow private ItemStack scriptedResult;
    @Shadow protected RecipeGridHandler.GroupedItems groupedItems;
    private MechanicalCrafterBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/crafter/RecipeGridHandler;tryToApplyRecipe(Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/kinetics/crafter/RecipeGridHandler$GroupedItems;)Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack sdm$tick(Level world, RecipeGridHandler.GroupedItems items) {
        items.calcStats();
        CraftingContainer craftinginventory = new MechanicalCraftingInventory(items);
        ItemStack result = null;
        RegistryAccess registryAccess = world.registryAccess();
        if ((Boolean) AllConfigs.server().recipes.allowRegularCraftingInCrafter.get()) {
            result = (ItemStack)world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftinginventory, world).filter((r) -> {
                return RecipeGridHandler.isRecipeAllowed(r, craftinginventory);
            }).map((r) -> {
                return r.assemble(craftinginventory, registryAccess);
            }).orElse((ItemStack) null);
        }

        if (result == null) {
            Optional<Recipe<CraftingContainer>> recipe = AllRecipeTypes.MECHANICAL_CRAFTING.find(craftinginventory, world);
            if(recipe.isPresent()) {

                Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
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
