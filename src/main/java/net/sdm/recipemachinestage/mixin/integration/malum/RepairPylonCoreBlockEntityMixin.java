package net.sdm.recipemachinestage.mixin.integration.malum;


import com.sammy.malum.common.block.curiosities.repair_pylon.RepairPylonCoreBlockEntity;
import com.sammy.malum.common.block.storage.IMalumSpecialItemAccessPoint;
import com.sammy.malum.common.recipe.SpiritRepairRecipe;
import com.sammy.malum.registry.common.recipe.RecipeTypeRegistry;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import team.lodestar.lodestone.systems.blockentity.LodestoneBlockEntityInventory;

import java.util.Optional;

@Mixin(value = RepairPylonCoreBlockEntity.class, remap = false)
public class RepairPylonCoreBlockEntityMixin {

    @Shadow public LodestoneBlockEntityInventory inventory;
    @Shadow public LodestoneBlockEntityInventory spiritInventory;
    public RepairPylonCoreBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "tryRepair(Lcom/sammy/malum/common/block/storage/IMalumSpecialItemAccessPoint;)Z", at = @At(value = "INVOKE", target = "Lcom/sammy/malum/common/recipe/SpiritRepairRecipe;getRecipe(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Ljava/util/List;)Lcom/sammy/malum/common/recipe/SpiritRepairRecipe;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void sdm$tryRepair(IMalumSpecialItemAccessPoint provider, CallbackInfoReturnable<Boolean> cir, LodestoneBlockEntityInventory inventoryForAltar){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(RecipeTypeRegistry.SPIRIT_REPAIR.get())) return;

        SpiritRepairRecipe recipe = SpiritRepairRecipe.getRecipe(this.thisEntity.getLevel(), inventoryForAltar.getStackInSlot(0), this.inventory.getStackInSlot(0), this.spiritInventory.nonEmptyItemStacks);
        Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                if(player != null) {
                    if(!player.hasStage(recipeBlockType.stage)) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }

        cir.setReturnValue(recipe != null);
    }
}
