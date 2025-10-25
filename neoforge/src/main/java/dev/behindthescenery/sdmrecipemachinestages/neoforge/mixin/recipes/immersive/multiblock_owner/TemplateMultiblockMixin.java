package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive.multiblock_owner;

import blusunrize.immersiveengineering.api.multiblocks.BlockMatcher;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveTemplateMultiblockPatch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(TemplateMultiblock.class)
public abstract class TemplateMultiblockMixin implements MultiblockHandler.IMultiblock, ImmersiveTemplateMultiblockPatch {


    @Shadow
    protected abstract BlockState applyToState(BlockState in, Mirror m, Rotation r);

    @Shadow
    @Final
    protected List<BlockMatcher.MatcherPredicate> additionalPredicates;

    @Shadow
    protected abstract List<Mirror> getPossibleMirrorStates();

    @Shadow
    @Final
    protected BlockPos triggerFromOrigin;

    @Shadow
    @Final
    protected BlockPos masterFromOrigin;

    @Shadow
    protected abstract void replaceStructureBlock(StructureTemplate.StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster);

    /**
     * @author Sixik
     * @reason Change Logic
     */
    @Overwrite
    public boolean createStructure(Level world, BlockPos pos, Direction side, Player player) {
        final Rotation rot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, side.getOpposite());
        if (rot == null)
            return false;
        final List<StructureTemplate.StructureBlockInfo> structure = getStructure(world);
        mirrorLoop:
        for (Mirror mirror : getPossibleMirrorStates()) {
            final StructurePlaceSettings placeSet = new StructurePlaceSettings().setMirror(mirror).setRotation(rot);
            final BlockPos origin = pos.subtract(StructureTemplate.calculateRelativePosition(placeSet, triggerFromOrigin));
            for (StructureTemplate.StructureBlockInfo info : structure) {
                final BlockPos realRelPos = StructureTemplate.calculateRelativePosition(placeSet, info.pos());
                final BlockPos here = origin.offset(realRelPos);

                final BlockState expected = applyToState(info.state(), mirror, rot);
                final BlockState inWorld = world.getBlockState(here);
                if (!BlockMatcher.matches(expected, inWorld, world, here, additionalPredicates).isAllow())
                    continue mirrorLoop;
            }
            if (!world.isClientSide)
                bts$form(world, origin, rot, mirror, side, player);
            return true;
        }
        return false;
    }

    @Override
    public void bts$form(Level world, BlockPos pos, Rotation rot, Mirror mirror, Direction sideHit, Player player) {
        BlockPos masterPos = TemplateMultiblock.withSettingsAndOffset(pos, masterFromOrigin, mirror, rot);
        for(StructureTemplate.StructureBlockInfo block : getStructure(world))
        {
            BlockPos actualPos = TemplateMultiblock.withSettingsAndOffset(pos, block.pos(), mirror, rot);
            bts$replaceStructureBlock(block, world, actualPos, mirror!=Mirror.NONE, sideHit,
                    actualPos.subtract(masterPos), player);
        }
    }
}
