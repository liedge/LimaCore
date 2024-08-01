package liedge.limacore.blockentity;

import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.packet.ClientboundBlockEntityDataPacket;
import liedge.limacore.network.packet.ServerboundBlockEntityDataRequestPacket;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Objects;

public abstract class LimaBlockEntity extends BlockEntity implements DataWatcherHolder
{
    private final LimaBlockEntityType<?> type;
    private final List<LimaDataWatcher<?>> dataWatchers;

    protected LimaBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.type = type;
        this.dataWatchers = defineDataWatchers();
    }

    public boolean canPlayerUse(Player player)
    {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr((double) worldPosition.getX() + 0.5d, (double) worldPosition.getY() + 0.5d, (double) worldPosition.getZ() + 0.5d) <= 64;
    }

    @Override
    public final List<LimaDataWatcher<?>> getDataWatchers()
    {
        return dataWatchers;
    }

    @Override
    public <T> void sendDataWatcherPacket(int index, NetworkSerializer<T> streamCodec, T data)
    {
        ServerLevel level = nonNullServerLevel();
        BlockPos blockPos = getBlockPos();
        PacketDistributor.sendToPlayersTrackingChunk(level, level.getChunkAt(blockPos).getPos(), new ClientboundBlockEntityDataPacket<>(blockPos, index, streamCodec, data));
    }

    @Override
    public LimaBlockEntityType<?> getType()
    {
        return type;
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        if (checkClientSide())
        {
            PacketDistributor.sendToServer(new ServerboundBlockEntityDataRequestPacket(this.getBlockPos()));
        }
    }

    // Override here to avoid needing warning suppression in subclasses
    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(CompoundTag tag) {}

    public ServerLevel nonNullServerLevel()
    {
        return LimaCoreUtil.castOrThrow(ServerLevel.class, nonNullLevel());
    }

    public Level nonNullLevel()
    {
        return Objects.requireNonNull(level, "Attempted to access block entity level before it has been assigned.");
    }

    public RegistryAccess nonNullRegistryAccess()
    {
        return nonNullLevel().registryAccess();
    }

    public boolean checkServerSide()
    {
        return level != null && !level.isClientSide();
    }

    public boolean checkClientSide()
    {
        return level != null && level.isClientSide();
    }

    protected List<LimaDataWatcher<?>> defineDataWatchers()
    {
        return List.of();
    }

    protected void tickServer(Level level, BlockPos pos, BlockState state)
    { }

    protected void tickClient(Level level, BlockPos pos, BlockState state)
    { }

    public static BlockEntityTicker<LimaBlockEntity> createServerTicker()
    {
        return (level, pos, state, be) -> {
          be.tickServer(level, pos, state);
          be.tickDataWatchers(false);
        };
    }

    public static BlockEntityTicker<LimaBlockEntity> createClientTicker()
    {
        return (level, pos, state, be) -> be.tickClient(level, pos, state);
    }
}