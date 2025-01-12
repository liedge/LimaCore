package liedge.limacore.blockentity;

import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.packet.ClientboundBlockEntityDataWatcherPacket;
import liedge.limacore.network.packet.ServerboundBlockEntityDataRequestPacket;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class LimaBlockEntity extends BlockEntity implements DataWatcherHolder, LimaBlockEntityAccess
{
    private final List<LimaDataWatcher<?>> dataWatchers;

    protected LimaBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.dataWatchers = createDataWatchers();
    }

    public boolean canPlayerUse(Player player)
    {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr((double) worldPosition.getX() + 0.5d, (double) worldPosition.getY() + 0.5d, (double) worldPosition.getZ() + 0.5d) <= 64;
    }

    @Override
    public final LimaBlockEntity getAsLimaBlockEntity()
    {
        return this;
    }

    @Override
    public final List<LimaDataWatcher<?>> getDataWatchers()
    {
        return dataWatchers;
    }

    @Override
    public <T> void sendDataWatcherPacket(int index, NetworkSerializer<T> serializer, T data)
    {
        ServerLevel level = nonNullServerLevel();
        BlockPos blockPos = getBlockPos();
        PacketDistributor.sendToPlayersTrackingChunk(level, level.getChunkAt(blockPos).getPos(), new ClientboundBlockEntityDataWatcherPacket<>(blockPos, index, serializer, data));
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket()
    {
        CompoundTag updateTag = getUpdateTag(nonNullRegistryAccess());
        if (!updateTag.isEmpty())
        {
            return ClientboundBlockEntityDataPacket.create(this, ($1, $2) -> updateTag);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider)
    {
        handleUpdateTag(pkt.getTag(), lookupProvider);
    }

    @Override
    public LimaBlockEntityType<?> getType()
    {
        return (LimaBlockEntityType<?>) super.getType();
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        if (level != null)
        {
            if (level.isClientSide)
            {
                PacketDistributor.sendToServer(new ServerboundBlockEntityDataRequestPacket(this.getBlockPos()));
                onLoadClient(level);
            }
            else
            {
                onLoadServer(LimaCoreUtil.castOrThrow(ServerLevel.class, level));
            }
        }
    }

    protected void onLoadClient(Level level) {}

    protected void onLoadServer(ServerLevel level) {}

    // Override here to avoid needing warning suppression in subclasses
    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(CompoundTag tag) {}

    //#region Block/level interaction helpers
    protected <T> BlockCapabilityCache<T, Direction> createCapabilityCache(BlockCapability<T, Direction> capability, ServerLevel level, Direction side, Runnable invalidationListener)
    {
        BlockPos neighborPos = getBlockPos().relative(side);
        return BlockCapabilityCache.create(capability, level, neighborPos, side.getOpposite(), () -> !this.isRemoved(), invalidationListener);
    }

    protected <T> BlockCapabilityCache<T, Direction> createCapabilityCache(BlockCapability<T, Direction> capability, ServerLevel level, Direction side)
    {
        return createCapabilityCache(capability, level, side, () -> {});
    }

    public void onPlacedByPlayer(Level level, Player player, ItemStack blockItem)
    {
        if (this instanceof BlockEntityWithOwner ownable) ownable.setOwner(player);
    }

    /**
     * Called by {@link liedge.limacore.block.LimaEntityBlock} in {@link liedge.limacore.block.LimaEntityBlock#onBlockStateChange(LevelReader, BlockPos, BlockState, BlockState)}
     * when the block state is updated. In other words, the old state and the new state is still the same block, only state properties have changed.
     * @param pos The position of the block
     * @param oldState The old block state
     * @param newState The new block state
     */
    public void onBlockStateUpdated(BlockPos pos, BlockState oldState, BlockState newState) {}
    //#endregion

    public ServerLevel nonNullServerLevel()
    {
        return LimaCoreUtil.castOrThrow(ServerLevel.class, level, "Attempted to access server level on client or before it has been assigned.");
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

    protected void tickServer(Level level, BlockPos pos, BlockState state)
    { }

    protected void tickClient(Level level, BlockPos pos, BlockState state)
    { }

    public static BlockEntityTicker<LimaBlockEntity> createServerTicker()
    {
        return (level, pos, state, be) -> {
          be.tickServer(level, pos, state);
          be.tickDataWatchers();
        };
    }

    public static BlockEntityTicker<LimaBlockEntity> createClientTicker()
    {
        return (level, pos, state, be) -> be.tickClient(level, pos, state);
    }
}