package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create.addons.the_factory_must_grow;


import com.drmangotea.tfmg.content.machinery.oil_processing.distillation_tower.controller.DistillationControllerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = DistillationControllerBlockEntity.class, remap = false)
public abstract class DistillationControllerBlockEntityMixin extends SmartBlockEntity {

    protected DistillationControllerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(method = "getMatchingRecipes", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/recipe/RecipeFinder;get(Ljava/lang/Object;Lnet/minecraft/world/level/Level;Ljava/util/function/Predicate;)Ljava/util/List;"))
    protected List<RecipeHolder<? extends Recipe<?>>> getMatchingRecipes(Object cacheKey, Level level, Predicate<RecipeHolder<? extends Recipe<?>>> recipe) {
        return RMSUtils.filterRecipes(RecipeFinder.get(cacheKey, level, recipe), RMSUtils.getBlockOwner(this));
    }
}
