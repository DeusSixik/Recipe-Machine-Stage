package net.sdm.recipemachinestage.mixin.integration.industrial_foregoing;

import com.buuz135.industrial.block.resourceproduction.tile.FluidLaserBaseTile;
import com.hrznstudio.titanium.util.RecipeUtil;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(value = FluidLaserBaseTile.class, remap = false)
public class FluidLaserBaseTileMixin {

    private FluidLaserBaseTile thisEntity = RecipeStagesUtil.cast(this);


    @Redirect(method = "onWork", at = @At(value = "INVOKE", target = "Lcom/hrznstudio/titanium/util/RecipeUtil;getRecipes(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeType;)Ljava/util/List;"))
    private<T extends Recipe<?>> List<T> sdm$onWork(Level world, RecipeType<T> typedRecipes){
        List<T> recipes = RecipeUtil.getRecipes(world, typedRecipes);
        if(!StageContainer.hasRecipes(typedRecipes)) return recipes;

        Iterator<T> iterator = recipes.iterator();

        Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
            if(player != null) {
                while (iterator.hasNext()) {
                    T recipe = iterator.next();
                    RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                    if(recipeBlockType != null) {
                        if(!player.hasStage(recipeBlockType.stage)) {
                            iterator.remove();
                        }
                    }
                }
            }
        }

        return recipes;
    }
}
