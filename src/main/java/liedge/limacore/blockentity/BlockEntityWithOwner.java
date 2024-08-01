package liedge.limacore.blockentity;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface BlockEntityWithOwner
{
    @Nullable
    Player getOwner();

    void setOwner(@Nullable Player player);
}