package net.sdm.recipemachinestage.mixin.integration.create;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
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

import java.util.Optional;

@Mixin(value = MillstoneBlockEntity.class)
public class MillstoneBlockEntityMixin {

    private MillstoneBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Redirect(method = "tick",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/AllRecipeTypes;find(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"), remap = false)
    public <C extends Container, T extends Recipe<C>> Optional<T> sdm$tick(AllRecipeTypes instance, C inv, Level world){
        Optional<T> recipe = world.getRecipeManager().getRecipeFor(instance.getType(), inv, world);
        if(recipe.isPresent() && StageContainer.hasRecipes(instance.getType())) {
            Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.get().getType(), recipe.get().getId());
                if (recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if (player != null) {
                        if (!player.hasStage(recipeBlockType.stage)) {
                            return Optional.empty();
                        }
                    }
                }
            }
        }
        return recipe;
    }

    @Redirect(method = "process",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/AllRecipeTypes;find(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"), remap = false)
    public <C extends Container, T extends Recipe<C>> Optional<T> sdm$process(AllRecipeTypes instance, C inv, Level world){
        Optional<T> recipe = world.getRecipeManager().getRecipeFor(instance.getType(), inv, world);
        if(recipe.isPresent() && StageContainer.hasRecipes(instance.getType())) {
            Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.get().getType(), recipe.get().getId());
                if (recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if (player != null) {
                        if (!player.hasStage(recipeBlockType.stage)) {
                            return Optional.empty();
                        }
                    }
                }
            }
        }
        return recipe;
    }

    @Redirect(method = "canProcess",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/AllRecipeTypes;find(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"),remap = false)
    public <C extends Container, T extends Recipe<C>> Optional<T> sdm$canProcess(AllRecipeTypes instance, C inv, Level world){
        Optional<T> recipe = world.getRecipeManager().getRecipeFor(instance.getType(), inv, world);
        if(recipe.isPresent() && StageContainer.hasRecipes(instance.getType())) {
            Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.get().getType(), recipe.get().getId());
                if (recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if (player != null) {
                        if (!player.hasStage(recipeBlockType.stage)) {
                            return Optional.empty();
                        }
                    }
                }
            }
        }
        return recipe;
    }
}
