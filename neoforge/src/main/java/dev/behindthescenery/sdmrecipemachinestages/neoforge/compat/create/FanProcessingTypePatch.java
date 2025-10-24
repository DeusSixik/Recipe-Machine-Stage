package dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.create;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface FanProcessingTypePatch {

    @Nullable List<ItemStack> bts$process(ItemStack stack, Level level, UUID ownerId);

    static FanProcessingTypePatch getPatch(FanProcessingType type) {
        if(type instanceof FanProcessingTypePatch patch)
            return patch;

        throw new NotImplementedException("FanProcessingType: " + type.getClass().getName() + " not implement 'FanProcessingTypePatch'");
    }
}
