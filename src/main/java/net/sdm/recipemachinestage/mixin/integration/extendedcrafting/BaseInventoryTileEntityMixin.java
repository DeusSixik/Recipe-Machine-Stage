package net.sdm.recipemachinestage.mixin.integration.extendedcrafting;

import com.blakebr0.cucumber.tileentity.BaseInventoryTileEntity;
import net.minecraft.nbt.CompoundTag;
import net.sdm.recipemachinestage.api.capability.IOwnerableSupport;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseInventoryTileEntity.class)
public class BaseInventoryTileEntityMixin {

    @Unique
    private BaseInventoryTileEntity recipe_machine_stage$thisBlock = RecipeStagesUtil.cast(this);

    @Inject(method = "load", at = @At("RETURN"))
    public void sdm$load(CompoundTag compoundTag, CallbackInfo ci){
        if(recipe_machine_stage$thisBlock instanceof IOwnerableSupport ownerableSupport) {
            if(compoundTag.contains("rms_owner"))
                ownerableSupport.recipe_machine_stage$getBlockOwnerCapability().read(compoundTag.getCompound("rms_owner"));
        }
    }

    @Inject(method = "saveAdditional", at = @At("RETURN"))
    public void sdm$saveAdditional(CompoundTag compoundTag, CallbackInfo ci){
        if(recipe_machine_stage$thisBlock instanceof IOwnerableSupport ownerableSupport) {
            CompoundTag nbt = new CompoundTag();
            ownerableSupport.recipe_machine_stage$getBlockOwnerCapability().write(nbt);
            compoundTag.put("rms_owner",nbt);
        }
    }
}
