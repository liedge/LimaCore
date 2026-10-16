package liedge.limacore.blockentity;

import liedge.limacore.network.sync.NullableValueTracker;
import liedge.limacore.network.sync.ValueTracker;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
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
        Level level = getLevel();
        UUID uid = getOwnerUUID();

        if (level != null && uid != null)
        {
            return level.getPlayerInAnyDimension(uid);
        }

        return null;
    }

    default void setOwner(@Nullable Player player)
    {
        UUID uuid = player != null ? player.getUUID() : null;
        setOwnerUUID(uuid);
    }

    default ValueTracker<Optional<UUID>> syncOwner()
    {
        // not auto, not expected to change (probably)
        return NullableValueTracker.create(LimaCoreNetworkSerializers.OPTIONAL_UUID, this::getOwnerUUID, this::setOwnerUUID);
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