package liedge.limacore.blockentity;

import liedge.limacore.util.LimaNbtUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static liedge.limacore.LimaCommonConstants.KEY_OWNER;

public interface OwnableBlockEntity extends LimaBlockEntityAccess
{
    @Nullable
    UUID getOwnerUUID();

    void setOwnerUUID(@Nullable UUID ownerUUID);

    @Nullable
    default Player getOwner()
    {
        UUID uuid = getOwnerUUID();
        return uuid != null ? nonNullLevel().getPlayerByUUID(uuid) : null;
    }

    default void setOwner(@Nullable Player player)
    {
        UUID uuid = player != null ? player.getUUID() : null;
        setOwnerUUID(uuid);
    }

    default void saveOwnerID(CompoundTag tag)
    {
        LimaNbtUtil.putOptionalUUID(tag, KEY_OWNER, getOwnerUUID());
    }

    default void loadOwnerID(CompoundTag tag)
    {
        setOwnerUUID(LimaNbtUtil.getOptionalUUID(tag, KEY_OWNER));
    }
}