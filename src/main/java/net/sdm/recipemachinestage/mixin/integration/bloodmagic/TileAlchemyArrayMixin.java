package net.sdm.recipemachinestage.mixin.integration.bloodmagic;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wayoftime.bloodmagic.common.alchemyarray.AlchemyArrayEffect;
import wayoftime.bloodmagic.common.tile.TileAlchemyArray;
import wayoftime.bloodmagic.core.registry.AlchemyArrayRegistry;
import wayoftime.bloodmagic.impl.BloodMagicAPI;
import wayoftime.bloodmagic.recipe.RecipeAlchemyArray;

import java.util.Optional;

@Mixin(value = TileAlchemyArray.class)
public class TileAlchemyArrayMixin {

    private TileAlchemyArray thisEntity = RecipeStagesUtil.cast(this);


    @Redirect(method = "attemptCraft", at = @At(value = "INVOKE", target = "Lwayoftime/bloodmagic/core/registry/AlchemyArrayRegistry;getEffect(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Lwayoftime/bloodmagic/common/alchemyarray/AlchemyArrayEffect;"), remap = false)
    private AlchemyArrayEffect sdm$attemptCraft(Level world, ItemStack input, ItemStack catalyst) {
        return recipe_machine_stage$getEffect(world,input,catalyst);
    }

    @Unique
    private AlchemyArrayEffect recipe_machine_stage$getEffect(Level world, ItemStack input, ItemStack catalyst) {
        Pair<Boolean, RecipeAlchemyArray> array = BloodMagicAPI.INSTANCE.getRecipeRegistrar().getAlchemyArray(world, input, catalyst);

        if(array != null && array.getRight() != null) {
            RecipeAlchemyArray recipe = array.getRight();
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
        }

        return array != null && array.getRight() != null && (Boolean)array.getLeft() ? AlchemyArrayRegistry.getEffect(world, ((RecipeAlchemyArray)array.getRight()).getId(), (RecipeAlchemyArray)array.getRight()) : null;
    }
}
