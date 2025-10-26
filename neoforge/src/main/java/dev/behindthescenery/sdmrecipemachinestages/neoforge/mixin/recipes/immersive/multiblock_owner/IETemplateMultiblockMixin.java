package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive.multiblock_owner;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.BlockMatcher;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBMemorizeStructure;
import blusunrize.immersiveengineering.common.util.IELogger;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveMultiblockBlockEntityCommonPatch;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveTemplateMultiblockPatch;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(IETemplateMultiblock.class)
public abstract class IETemplateMultiblockMixin extends TemplateMultiblock implements ImmersiveTemplateMultiblockPatch {

    @Shadow
    @Final
    private MultiblockRegistration<?> logic;

    protected IETemplateMultiblockMixin(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, List<BlockMatcher.MatcherPredicate> additionalPredicates) {
        super(loc, masterFromOrigin, triggerFromOrigin, size, additionalPredicates);
    }

    @Override
    public void bts$replaceStructureBlock(StructureTemplate.StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster, Player player) {
        bts$replaceStructureBlockCopy(info, world, actualPos, mirrored, clickDirection, offsetFromMaster, player);
    }

    @Override
    public void bts$replaceStructureBlockCopy(StructureTemplate.StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster, Player player) {
        BlockState newState = logic.block().get().defaultBlockState();
        newState = newState.setValue(IEProperties.MULTIBLOCKSLAVE, !offsetFromMaster.equals(Vec3i.ZERO));
        if(newState.hasProperty(IEProperties.ACTIVE))
            newState = newState.setValue(IEProperties.ACTIVE, false);
        if(newState.hasProperty(IEProperties.MIRRORED))
            newState = newState.setValue(IEProperties.MIRRORED, mirrored);
        if(newState.hasProperty(IEProperties.FACING_HORIZONTAL))
            newState = newState.setValue(IEProperties.FACING_HORIZONTAL, clickDirection.getOpposite());
        final BlockState oldState = world.getBlockState(actualPos);
        world.setBlock(actualPos, newState, 0);
        BlockEntity curr = world.getBlockEntity(actualPos);
        if(curr instanceof MultiblockBlockEntityDummy<?> dummy)
            dummy.getHelper().setPositionInMB(info.pos());
        else if(!(curr instanceof MultiblockBlockEntityMaster<?>))
            IELogger.logger.error("Expected MB TE at {} during placement", actualPos);

        if(curr instanceof MultiblockBlockEntityMaster<?> master)
            ((ImmersiveMultiblockBlockEntityCommonPatch)master).sdm$im_setOwner(RMSUtils.getPlayerId(player));

        RMSUtils.setBlockEntityOwner(curr, player);

        IMultiblockBEHelper<IMultiblockState> helper = ((IMultiblockBE<IMultiblockState>)curr).getHelper();
        if(helper.getMultiblock().logic() instanceof MBMemorizeStructure<IMultiblockState> memo)
            memo.setMemorizedBlockState(helper.getState(), info.pos(), oldState);
        final LevelChunk chunk = world.getChunkAt(actualPos);
        world.markAndNotifyBlock(actualPos, chunk, oldState, newState, Block.UPDATE_ALL, 512);
    }
}
