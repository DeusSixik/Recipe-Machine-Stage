package dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public interface ImmersiveTemplateMultiblockPatch {

    void bts$form(Level world, BlockPos pos, Rotation rot, Mirror mirror, Direction sideHit, Player player);

    void bts$replaceStructureBlock(StructureTemplate.StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster, Player player);

    void bts$replaceStructureBlockCopy(StructureTemplate.StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster, Player player);
}
