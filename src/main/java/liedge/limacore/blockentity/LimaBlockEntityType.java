package liedge.limacore.blockentity;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class LimaBlockEntityType<BE extends LimaBlockEntity> extends BlockEntityType<BE>
{
    public static <BE extends LimaBlockEntity> LimaBlockEntityType<BE> of(WithTypeConstructor<BE> constructor, Holder<Block> holder)
    {
        return new LimaBlockEntityType<>(constructor, Set.of(holder.value()));
    }

    public static <BE extends LimaBlockEntity> Builder<BE> builder(WithTypeConstructor<BE> constructor)
    {
        return new Builder<>(constructor);
    }

    private final WithTypeConstructor<BE> constructor;

    @SuppressWarnings("ConstantConditions")
    protected LimaBlockEntityType(WithTypeConstructor<BE> constructor, Set<Block> validBlocks)
    {
        super(null, validBlocks, null);
        this.constructor = constructor;
    }

    @Override
    public BE create(BlockPos pos, BlockState state)
    {
        return constructor.newInstance(this, pos, state);
    }

    public @Nullable <T> T getDataMap(DataMapType<BlockEntityType<?>, T> dataMapType)
    {
        return Objects.requireNonNull(builtInRegistryHolder()).getData(dataMapType);
    }

    public <T> T getDataMapOrDefault(DataMapType<BlockEntityType<?>, T> dataMapType, T fallback)
    {
        return Objects.requireNonNullElse(getDataMap(dataMapType), fallback);
    }

    @FunctionalInterface
    public interface WithTypeConstructor<BE extends LimaBlockEntity>
    {
        BE newInstance(LimaBlockEntityType<? extends BE> type, BlockPos pos, BlockState state);
    }

    public static class Builder<BE extends LimaBlockEntity>
    {
        private final WithTypeConstructor<BE> constructor;
        private final ObjectSet<Block> validBlocks = new ObjectOpenHashSet<>();

        protected Builder(WithTypeConstructor<BE> constructor)
        {
            this.constructor = constructor;
        }

        public Builder<BE> withBlock(Block block)
        {
            validBlocks.add(block);
            return this;
        }

        public Builder<BE> withBlock(Holder<Block> holder)
        {
            return withBlock(holder.value());
        }

        public LimaBlockEntityType<BE> build()
        {
            Preconditions.checkState(!validBlocks.isEmpty(), "Valid blocks for block entity type cannot be empty.");
            return new LimaBlockEntityType<>(constructor, ObjectSets.unmodifiable(validBlocks));
        }
    }
}