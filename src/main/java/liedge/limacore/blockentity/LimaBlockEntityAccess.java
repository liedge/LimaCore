package liedge.limacore.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Provides base properties/methods of block entities. Extend only, do not implement.
 * Only implemented by {@link LimaBlockEntity} and its subtypes.
 */
public interface LimaBlockEntityAccess
{
    LimaBlockEntity getAsLimaBlockEntity();

    BlockPos getBlockPos();

    BlockState getBlockState();

    void setChanged();

    @Nullable Level getLevel();

    Level nonNullLevel();

    ServerLevel nonNullServerLevel();
}