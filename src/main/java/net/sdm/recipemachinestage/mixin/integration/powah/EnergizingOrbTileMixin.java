package net.sdm.recipemachinestage.mixin.integration.powah;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import owmii.powah.block.energizing.EnergizingOrbTile;
import owmii.powah.lib.logistics.inventory.RecipeWrapper;
import owmii.powah.recipe.Recipes;

import java.util.Optional;

@Mixin(value = EnergizingOrbTile.class)
public class EnergizingOrbTileMixin {

    private EnergizingOrbTile thisEntity = RecipeStagesUtil.cast(this);

    @Redirect(method = "checkRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    public <C extends Container, T extends Recipe<C>> Optional<T> sdm$checkRecipe(RecipeManager instance, RecipeType<T> p_44016_, C p_44017_, Level p_44018_){
        Optional<T> recipeOptional = thisEntity.getLevel().getRecipeManager().getRecipeFor((RecipeType)Recipes.ENERGIZING.get(), new RecipeWrapper(thisEntity.getInventory()), thisEntity.getLevel());

        if(!StageContainer.hasRecipes(Recipes.ENERGIZING.get())) return recipeOptional;

        Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && recipeOptional.isPresent()) {
            T recipe = recipeOptional.get();
            IOwnerBlock ownerBlock = d1.get();
            PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
            if(player != null) {
                RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if (recipeBlockType != null && !player.hasStage(recipeBlockType.stage)) {
                    return Optional.empty();
                }
            }
        }

        return recipeOptional;
    }
}
