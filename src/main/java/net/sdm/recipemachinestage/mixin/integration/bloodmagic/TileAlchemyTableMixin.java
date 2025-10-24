package net.sdm.recipemachinestage.mixin.integration.bloodmagic;


import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wayoftime.bloodmagic.common.tile.TileAlchemyTable;
import wayoftime.bloodmagic.impl.BloodMagicAPI;
import wayoftime.bloodmagic.impl.BloodMagicRecipeRegistrar;
import wayoftime.bloodmagic.recipe.EffectHolder;
import wayoftime.bloodmagic.recipe.RecipeAlchemyTable;
import wayoftime.bloodmagic.recipe.flask.RecipePotionFlaskBase;

import java.util.List;
import java.util.Optional;

@Mixin(value = TileAlchemyTable.class, remap = false)
public class TileAlchemyTableMixin {

    private TileAlchemyTable thisEntity = RecipeStagesUtil.cast(this);

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lwayoftime/bloodmagic/impl/BloodMagicRecipeRegistrar;getAlchemyTable(Lnet/minecraft/world/level/Level;Ljava/util/List;)Lwayoftime/bloodmagic/recipe/RecipeAlchemyTable;"))
    private RecipeAlchemyTable sdm$tick$getAlchemyTable(BloodMagicRecipeRegistrar instance, Level j, List<ItemStack> matched) {
        RecipeAlchemyTable recipeAlchemyTable = BloodMagicAPI.INSTANCE.getRecipeRegistrar().getAlchemyTable(j, matched);

        if(recipeAlchemyTable != null) {
            Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer()!= null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipeAlchemyTable.getType(), recipeAlchemyTable.getId());
                if(recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!player.hasStage(recipeBlockType.stage)) {
                            return null;
                        }
                    }
                }
            }
        }

        return recipeAlchemyTable;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lwayoftime/bloodmagic/impl/BloodMagicRecipeRegistrar;getPotionFlaskRecipe(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Ljava/util/List;Ljava/util/List;)Lwayoftime/bloodmagic/recipe/flask/RecipePotionFlaskBase;"))
    private RecipePotionFlaskBase sdm$tick$getPotionFlaskRecipe(BloodMagicRecipeRegistrar instance, Level j, ItemStack matched, List<EffectHolder> i, List<ItemStack> prio) {
        RecipePotionFlaskBase recipe = BloodMagicAPI.INSTANCE.getRecipeRegistrar().getPotionFlaskRecipe(j, matched, i, prio);

        if(recipe != null && StageContainer.hasRecipes(recipe.getType())) {
            Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer()!= null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if(recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!player.hasStage(recipeBlockType.stage)) {
                            return null;
                        }
                    }
                }
            }
        }

        return recipe;
    }
}
