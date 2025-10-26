package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockOwnerData;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.create.AirCurrentPatch;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.utils.RMSCreateUtils;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(AirCurrent.class)
public class AirCurrentMixin implements AirCurrentPatch {

    @Unique
    private UUID bts$ownerId = BlockOwnerData.EMPTY;

    @Override
    public void bts$setOwnerBlock(UUID ownerId) {
        this.bts$ownerId = ownerId;
    }

    @Redirect(method = "tickAffectedEntities", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessing;applyProcessing(Lnet/minecraft/world/entity/item/ItemEntity;Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Z"))
    public boolean bts$tickAffectedEntities(ItemEntity entityIn, FanProcessingType additional) {
        return RMSCreateUtils.applyProcessing(entityIn, additional, bts$ownerId);
    }
}
