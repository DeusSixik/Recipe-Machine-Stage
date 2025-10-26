package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive.multiblock_owner;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.common.blocks.multiblocks.ImprovedBlastfurnaceMultiblock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.StoneMultiblock;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveTemplateMultiblockPatch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ImprovedBlastfurnaceMultiblock.class)
public abstract class ImprovedBlastfurnaceMultiblockMixin extends StoneMultiblock implements ImmersiveTemplateMultiblockPatch {

    protected ImprovedBlastfurnaceMultiblockMixin(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, MultiblockRegistration<?> logic) {
        super(loc, masterFromOrigin, triggerFromOrigin, size, logic);
    }

    @Override
    public void bts$replaceStructureBlock(StructureTemplate.StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster, Player player) {
        bts$replaceStructureBlockCopy(
                info, world,
                new BlockPos(actualPos.getX()-2*offsetFromMaster.getX(), actualPos.getY(), actualPos.getZ()-2*offsetFromMaster.getZ()),
                mirrored,
                clickDirection.getOpposite(),
                new Vec3i(-offsetFromMaster.getX(), offsetFromMaster.getY(), -offsetFromMaster.getZ()),
                player
        );
    }
}
