package net.sdm.recipemachinestage.mixin.integration.applied;

import appeng.blockentity.misc.ChargerBlockEntity;
import appeng.blockentity.misc.ChargerRecipes;
import appeng.recipes.handlers.ChargerRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChargerBlockEntity.class, remap = false)
public class ChargerBlockEntityMixin {

    private ChargerBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Redirect(method = "doWork", at = @At
        (
        value = "INVOKE",
        ordinal = 0,
        target = "Lappeng/blockentity/misc/ChargerRecipes;findRecipe(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lappeng/recipes/handlers/ChargerRecipe;"
        )
    )
    public ChargerRecipe sdm$doWork(Level level, ItemStack myItem){
        ChargerRecipe recipe = ChargerRecipes.findRecipe(level, myItem);

//        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(ChargerRecipe.TYPE))
//            return recipe;
//
//        if(recipe != null) {
//            Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
//            if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
//                IOwnerBlock ownerBlock = d1.get();
//                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
//                if(recipeBlockType != null) {
//                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
//                    if(player != null) {
//                        if(!player.hasStage(recipeBlockType.stage)) {
//                            return null;
//                        }
//                    }
//                }
//            }
//        }

        return RecipeStagesUtil.checkRecipe(recipe, thisEntity);
    }
}
