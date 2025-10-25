package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.malum;

import com.sammy.malum.common.block.curiosities.spirit_altar.SpiritAltarBlockEntity;
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
import team.lodestar.lodestone.systems.blockentity.LodestoneBlockEntity;

import java.util.List;

@Mixin(SpiritAltarBlockEntity.class)
public abstract class SpiritAltarBlockEntityMixin extends LodestoneBlockEntity {

    protected SpiritAltarBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(method = "recalculateRecipes",
            at = @At(value = "INVOKE", target = "Lteam/lodestar/lodestone/systems/recipe/LodestoneRecipeType;getRecipes(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeType;)Ljava/util/List;"))
    public <T extends RecipeInput, K extends Recipe<T>> List<K> sdm$tryRepair(Level level, RecipeType<K> recipeType){
        return RMSLodeStoneUtils.getRecipes(level, recipeType, RMSUtils.getBlockOwner(this));
    }
}
