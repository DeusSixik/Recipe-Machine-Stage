package net.sdm.recipemachinestage.mixin.integration.create.ore_excavation;

import com.tom.createores.block.entity.ExcavatingBlockEntityImpl;
import com.tom.createores.block.entity.ExtractorBlockEntity;
import com.tom.createores.recipe.ExtractorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ExtractorBlockEntity.class)
public class ExtractorBlockEntityMixin extends ExcavatingBlockEntityImpl<ExtractorRecipe> {

    @Unique
    private ExtractorBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    protected ExtractorBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "canExtract", at = @At(value = "RETURN"), cancellable = true, remap = false)
    public void smd$canExtract(CallbackInfoReturnable<Boolean> cir) {
        if (!StageContainer.hasRecipes(current.getType())) {
            return;
        }

        Optional<IOwnerBlock> owner = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();

        owner.ifPresent(o -> {
            if(thisEntity.getLevel() == null || thisEntity.getLevel().getServer() == null) return;

            RecipeBlockType recipeBlockType = StageContainer.getRecipeData(current.getType(), current.getId());
            if (recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), o.getOwner());
                if (player != null) {
                    if (!player.hasStage(recipeBlockType.stage)) {
                        cir.setReturnValue(false);
                    }
                }
            }
        });
    }

    @Shadow
    protected RecipeType<ExtractorRecipe> getRecipeType() {
        return null;
    }
}
