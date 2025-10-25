package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.malum;

import com.sammy.malum.common.block.curiosities.repair_pylon.RepairPylonCoreBlockEntity;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.utils.RMSLodeStoneUtils;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import team.lodestar.lodestone.systems.multiblock.MultiBlockCoreEntity;
import team.lodestar.lodestone.systems.multiblock.MultiBlockStructure;

import java.util.function.Predicate;

@Mixin(value = RepairPylonCoreBlockEntity.class, remap = false)
public class RepairPylonCoreBlockEntityMixin extends MultiBlockCoreEntity {


    protected RepairPylonCoreBlockEntityMixin(BlockEntityType<?> type, MultiBlockStructure structure, BlockPos pos, BlockState state) {
        super(type, structure, pos, state);
    }

    @Redirect(method = "updateRecipe(Ljava/util/function/Predicate;)Lcom/sammy/malum/common/recipe/SpiritRepairRecipe;",
            at = @At(value = "INVOKE", target = "Lteam/lodestar/lodestone/systems/recipe/LodestoneRecipeType;findRecipe(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeType;Ljava/util/function/Predicate;)Lnet/minecraft/world/item/crafting/Recipe;"))
    public <T extends RecipeInput, K extends Recipe<T>> K sdm$tryRepair(Level value, RecipeType<K> recipe, Predicate<K> level){
        return RMSLodeStoneUtils.findRecipe(value, recipe, level, RMSUtils.getBlockOwner(this));
    }
}
