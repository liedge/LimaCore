package liedge.limacore.blockentity;

import liedge.limacore.util.LimaNbtUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static liedge.limacore.LimaCommonConstants.KEY_OWNER;

public interface BlockEntityWithOwner
{
    @Nullable
    Player getOwner();

    @Nullable
    UUID getOwnerUUID();

    void setOwnerUUID(@Nullable UUID ownerUUID);

    void setOwner(@Nullable Player player);

    default void saveOwner(CompoundTag tag, HolderLookup.Provider registries)
    {
        LimaNbtUtil.putOptionalUUID(tag, KEY_OWNER, getOwnerUUID());
    }

    default void loadOwner(CompoundTag tag, HolderLookup.Provider registries)
    {
        UUID uuid = LimaNbtUtil.getOptionalUUID(tag, KEY_OWNER);
        setOwnerUUID(uuid);
    }
}