package liedge.limacore.blockentity;

import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

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

    default void saveOwnerID(ValueOutput output)
    {
        output.storeNullable(KEY_OWNER, UUIDUtil.CODEC, getOwnerUUID());
    }

    default void loadOwnerID(ValueInput input)
    {
        input.read(KEY_OWNER, UUIDUtil.CODEC).ifPresent(this::setOwnerUUID);
    }
}