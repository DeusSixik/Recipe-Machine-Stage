package net.sdm.recipemachinestage.mixin.integration.bloodmagic;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
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
import wayoftime.bloodmagic.common.tile.TileAlchemicalReactionChamber;
import wayoftime.bloodmagic.impl.BloodMagicAPI;
import wayoftime.bloodmagic.impl.BloodMagicRecipeRegistrar;
import wayoftime.bloodmagic.recipe.RecipeARC;

import java.util.Optional;

@Mixin(value = TileAlchemicalReactionChamber.class, remap = false)
public class TileAlchemicalReactionChamberMixin {

    private TileAlchemicalReactionChamber thisEntity = RecipeStagesUtil.cast(this);

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lwayoftime/bloodmagic/impl/BloodMagicRecipeRegistrar;getARC(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraftforge/fluids/FluidStack;)Lwayoftime/bloodmagic/recipe/RecipeARC;"))
    private RecipeARC sdm$tick$getARC(BloodMagicRecipeRegistrar instance, Level level, ItemStack world, ItemStack input, FluidStack arcToolInput) {
        RecipeARC recipe = BloodMagicAPI.INSTANCE.getRecipeRegistrar().getARC(level, world, input, arcToolInput);

        if(recipe != null && StageContainer.hasRecipes(recipe.getType())) {
            Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
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

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private <C extends Container, T extends Recipe<C>> Optional<T> sdm$tick$getRecipeFor(RecipeManager instance, RecipeType<T> p_44016_, C p_44017_, Level p_44018_) {
        Optional<T> recipeOptional = instance.getRecipeFor(p_44016_, p_44017_, p_44018_);

        if(recipeOptional.isPresent()) {
            T recipe = recipeOptional.get();
            Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer()!= null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if(recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!player.hasStage(recipeBlockType.stage)) {
                            return Optional.empty();
                        }
                    }
                }
            }
        }

        return recipeOptional;
    }
}
