package liedge.limacore.block;

import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.menu.LimaMenuProvider;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public abstract class LimaEntityBlock extends Block implements EntityBlock
{
    private LimaBlockEntityType<?> blockEntityType;

    protected LimaEntityBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        LimaBlockEntityType<?> type = getBlockEntityType(state);
        return type != null ? type.create(pos, state) : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> otherType)
    {
        if (getBlockEntityType(state) == otherType)
        {
            if (level.isClientSide() && shouldTickClient(state))
            {
                return (BlockEntityTicker<T>) LimaBlockEntity.createClientTicker();
            }
            else if (!level.isClientSide() && shouldTickServer(state))
            {
                return (BlockEntityTicker<T>) LimaBlockEntity.createServerTicker();
            }
        }

        return null;
    }

    @Override
    public @Nullable LimaMenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos)
    {
        return blockEntityMenuProvider(level, pos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        return tryOpenMenu(state, level, pos, player);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        LimaBlockEntityType<?> type = getBlockEntityType(state);

        if (type != null)
        {
            LimaBlockEntity blockEntity = type.getBlockEntity(level, pos);
            if (blockEntity != null)
            {
                blockEntity.saveToItem(stack, level.registryAccess());
            }
        }

        return stack;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (placer instanceof Player player)
        {
            LimaBlockEntity blockEntity = LimaBlockUtil.getSafeBlockEntity(level, pos, LimaBlockEntity.class);
            if (blockEntity != null) blockEntity.onPlacedByPlayer(level, player, stack);
        }
    }

    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState)
    {
        if (oldState.getBlock() == newState.getBlock() && newState.getBlock() == this)
        {
            LimaBlockEntity blockEntity = LimaBlockUtil.getSafeBlockEntity(level, pos, LimaBlockEntity.class);
            if (blockEntity != null) blockEntity.onBlockStateUpdated(pos, oldState, newState);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        // TODO: This will be replaced by native method.
        if (state.hasBlockEntity() && !state.is(newState.getBlock()))
        {
            LimaBlockEntity blockEntity = LimaBlockUtil.getSafeBlockEntity(level, pos, LimaBlockEntity.class);
            if (blockEntity != null) blockEntity.onRemovedFromLevel(level, pos, state, newState);
            level.removeBlockEntity(pos);
        }
    }

    // Override here to avoid needing warning suppression in subclasses
    @SuppressWarnings("deprecation")
    @Override
    protected RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    /**
     * Gets the {@link LimaBlockEntityType} that should be created for the given {@link BlockState}. By default,
     * this function ignores state and tries to find a type matching this block's registry ID.
     * @param state The block state
     * @return The block entity type, or null if the block should not create a BE.
     * @throws IllegalStateException If no {@link LimaBlockEntityType} was found in the block entity type registry.
     */
    public @Nullable LimaBlockEntityType<?> getBlockEntityType(BlockState state)
    {
        if (blockEntityType == null)
        {
            ResourceLocation id = LimaRegistryUtil.getBlockId(this);
            blockEntityType = LimaCoreUtil.castOrThrow(LimaBlockEntityType.class, LimaRegistryUtil.getNonNullRegistryValue(id, BuiltInRegistries.BLOCK_ENTITY_TYPE),
                    () -> new IllegalStateException("No valid block entity type matches block id '" + id + "'"));
        }

        return blockEntityType;
    }

    protected @Nullable LimaMenuProvider blockEntityMenuProvider(Level level, BlockPos pos)
    {
        LimaBlockEntity blockEntity = LimaBlockUtil.getSafeBlockEntity(level, pos, LimaBlockEntity.class);
        return blockEntity != null ? blockEntity.getType().createMenuProvider(blockEntity) : null;
    }

    protected InteractionResult tryOpenMenu(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (level.isClientSide())
        {
            return InteractionResult.SUCCESS;
        }
        else
        {
            if (!player.isCrouching())
            {
                LimaMenuProvider provider = getMenuProvider(state, level, pos);
                if (provider != null)
                {
                    provider.openMenuScreen(player);
                    return InteractionResult.CONSUME;
                }

            }

            return InteractionResult.PASS;
        }
    }

    protected boolean shouldTickClient(BlockState state)
    {
        return false;
    }

    protected boolean shouldTickServer(BlockState state)
    {
        return false;
    }
}