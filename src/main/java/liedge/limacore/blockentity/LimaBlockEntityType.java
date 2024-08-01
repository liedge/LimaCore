package liedge.limacore.blockentity;

import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;

public final class LimaBlockEntityType<BE extends LimaBlockEntity> extends BlockEntityType<BE>
{
    public static <BE extends LimaBlockEntity> LimaBlockEntityType<BE> forBlock(Constructor<BE> constructor, Block block1)
    {
        return new LimaBlockEntityType<>(constructor, Set.of(block1));
    }

    public static <BE extends LimaBlockEntity> LimaBlockEntityType<BE> forBlock(Constructor<BE> constructor, Holder<Block> holder)
    {
        return forBlock(constructor, holder.value());
    }

    public static <BE extends LimaBlockEntity> LimaBlockEntityType<BE> forBlocks(Constructor<BE> constructor, Block... blocks)
    {
        return new LimaBlockEntityType<>(constructor, Set.of(blocks));
    }

    @SafeVarargs
    public static <BE extends LimaBlockEntity> LimaBlockEntityType<BE> forBlocks(Constructor<BE> constructor, Holder<Block>... holders)
    {
        Set<Block> blocks = Arrays.stream(holders).map(Holder::value).collect(LimaCollectionsUtil.toUnmodifiableObjectSet());
        return new LimaBlockEntityType<>(constructor, blocks);
    }

    private final Constructor<BE> constructor;

    @SuppressWarnings("ConstantConditions")
    private LimaBlockEntityType(Constructor<BE> constructor, Set<Block> validBlocks)
    {
        super(null, validBlocks, null);
        this.constructor = constructor;
    }

    @Override
    public BE create(BlockPos pos, BlockState state)
    {
        return constructor.newInstance(this, pos, state);
    }

    @SuppressWarnings("unchecked")
    public @Nullable BE validateBlockEntity(@Nullable BlockEntity blockEntity)
    {
        if (blockEntity != null && blockEntity.getType().equals(this))
        {
            return (BE) blockEntity;
        }
        else
        {
            return null;
        }
    }

    @FunctionalInterface
    public interface Constructor<BE extends LimaBlockEntity>
    {
        BE newInstance(LimaBlockEntityType<? extends BE> type, BlockPos pos, BlockState state);
    }
}