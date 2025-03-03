package liedge.limacore.blockentity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Provides base properties/methods of block entities. Extend only, do not implement.
 * Only implemented by {@link LimaBlockEntity} and its subtypes.
 */
public interface LimaBlockEntityAccess
{
    LimaBlockEntity getAsLimaBlockEntity();

    void setChanged();

    @Nullable Level getLevel();

    Level nonNullLevel();

    ServerLevel nonNullServerLevel();
}