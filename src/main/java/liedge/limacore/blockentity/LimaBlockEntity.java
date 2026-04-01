package liedge.limacore.blockentity;

import com.mojang.logging.LogUtils;
import liedge.limacore.lib.Translatable;
import liedge.limacore.menu.BlockEntityMenuType;
import liedge.limacore.network.IndexedStreamData;
import liedge.limacore.network.packet.ClientboundBlockEntityDataWatcherPacket;
import liedge.limacore.network.packet.ServerboundBlockEntityDataRequestPacket;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.util.LimaCoreObjects;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;

public abstract class LimaBlockEntity extends BlockEntity implements DataWatcherHolder, LimaBlockEntityAccess
{
    private static final Logger LOGGER = LogUtils.getLogger();

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

    public Component getMenuTitle(BlockEntityMenuType<?, ?> menuType)
    {
        Translatable defaultTitle = menuType.getDefaultTitle();
        return defaultTitle != null ? defaultTitle.translate() : getBlockState().getBlock().getName();
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
    public void sendDataWatcherPacket(List<IndexedStreamData<?>> streamData)
    {
        ServerLevel level = nonNullServerLevel();
        BlockPos blockPos = getBlockPos();

        PacketDistributor.sendToPlayersTrackingChunk(level, level.getChunkAt(blockPos).getPos(), new ClientboundBlockEntityDataWatcherPacket(streamData, blockPos));
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket()
    {
        CompoundTag updateTag = getUpdateTag(nonNullLevel().registryAccess());
        if (!updateTag.isEmpty())
        {
            return ClientboundBlockEntityDataPacket.create(this, (_, _) -> updateTag);
        }
        else
        {
            return null;
        }
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
            if (level instanceof ServerLevel serverLevel)
            {
                onLoadServer(serverLevel);
            }
            else
            {
                ClientPacketDistributor.sendToServer(new ServerboundBlockEntityDataRequestPacket(this.getBlockPos()));
                onLoadClient(level);
            }
        }
    }

    protected void onLoadClient(Level level) {}

    protected void onLoadServer(ServerLevel level) {}

    // Override here to avoid needing warning suppression in subclasses
    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(ValueOutput output) {}

    @ApiStatus.Internal
    public void saveToItemStack(ItemStack stack, HolderLookup.Provider registries)
    {
        if (!(stack.getItem() instanceof BlockItem blockItem && this.getType().getValidBlocks().contains(blockItem.getBlock())))
        {
            LOGGER.warn("Attempted to save block entity data of type {} to incompatible item {}", LimaRegistryUtil.getNonNullRegistryId(getType(), BuiltInRegistries.BLOCK_ENTITY_TYPE), LimaRegistryUtil.getItemId(stack.getItem()));
            return;
        }

        CompoundTag tag;

        try (ProblemReporter.ScopedCollector reporter = createReporter())
        {
            TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
            saveCustomOnly(output);
            removeComponentsFromTag(output);
            tag = output.buildResult();
        }

        stack.applyComponents(collectComponents());

        TypedEntityData<BlockEntityType<?>> data = TypedEntityData.of(getType(), tag);
        stack.set(DataComponents.BLOCK_ENTITY_DATA, data);
    }

    protected final ProblemReporter.ScopedCollector createReporter()
    {
        return new ProblemReporter.ScopedCollector(problemPath(), LOGGER);
    }

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
        if (this instanceof OwnableBlockEntity ownable) ownable.setOwner(player);
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

    @Override
    public final Level nonNullLevel()
    {
        return Objects.requireNonNull(level, () -> String.format("Attempted to access level for block entity at %s before it has been assigned.", worldPosition.toShortString()));
    }

    @Override
    public final ServerLevel nonNullServerLevel()
    {
        return LimaCoreObjects.cast(ServerLevel.class, level, () ->
                new IllegalStateException(String.format("Attempted to access server level for block entity at %s on the client or before it has been assigned.", worldPosition.toShortString())));
    }

    public boolean checkServerSide()
    {
        return level != null && !level.isClientSide();
    }

    public boolean checkClientSide()
    {
        return level != null && level.isClientSide();
    }

    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    { }

    protected void tickClient(Level level, BlockPos pos, BlockState state)
    { }

    public static BlockEntityTicker<LimaBlockEntity> createServerTicker()
    {
        return (level, pos, state, be) -> {
          be.tickServer((ServerLevel) level, pos, state);
          be.tickDataWatchers();
        };
    }

    public static BlockEntityTicker<LimaBlockEntity> createClientTicker()
    {
        return (level, pos, state, be) -> be.tickClient(level, pos, state);
    }
}