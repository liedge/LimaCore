package liedge.limacore.blockentity;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class LimaBlockEntityType<BE extends LimaBlockEntity> extends BlockEntityType<BE>
{
    public static <BE extends LimaBlockEntity> LimaBlockEntityType<BE> of(BlockEntitySupplier<BE> factory, Holder<Block> holder)
    {
        return new LimaBlockEntityType<>(factory, Set.of(holder.value()));
    }

    public static <BE extends LimaBlockEntity> Builder<BE> builder(BlockEntitySupplier<BE> factory)
    {
        return new Builder<>(factory);
    }

    @SuppressWarnings("ConstantConditions")
    protected LimaBlockEntityType(BlockEntitySupplier<BE> factory, Set<Block> validBlocks)
    {
        super(factory, validBlocks, null);
    }

    public @Nullable <T> T getDataMap(DataMapType<BlockEntityType<?>, T> dataMapType)
    {
        return Objects.requireNonNull(builtInRegistryHolder()).getData(dataMapType);
    }

    public <T> T getDataMapOrDefault(DataMapType<BlockEntityType<?>, T> dataMapType, T fallback)
    {
        return Objects.requireNonNullElse(getDataMap(dataMapType), fallback);
    }

    public static abstract class AbstractBuilder<BE extends LimaBlockEntity, TYPE extends LimaBlockEntityType<BE>, B extends AbstractBuilder<BE, TYPE, B>>
    {
        private final BlockEntitySupplier<BE> factory;
        private final ObjectSet<Block> validBlocks = new ObjectOpenHashSet<>();

        protected AbstractBuilder(BlockEntitySupplier<BE> factory)
        {
            this.factory = factory;
        }

        public B withBlock(Block block)
        {
            validBlocks.add(block);
            return thisBuilder();
        }

        public B withBlock(Holder<Block> holder)
        {
            return withBlock(holder.value());
        }

        @SuppressWarnings("unchecked")
        protected B thisBuilder()
        {
            return (B) this;
        }

        public final TYPE build()
        {
            Preconditions.checkState(!validBlocks.isEmpty(), "Valid blocks cannot be empty.");
            return build(factory, ObjectSets.unmodifiable(validBlocks));
        }

        protected abstract TYPE build(BlockEntitySupplier<BE> factory, Set<Block> validBlocks);
    }

    public static class Builder<BE extends LimaBlockEntity> extends AbstractBuilder<BE, LimaBlockEntityType<BE>, Builder<BE>>
    {
        private Builder(BlockEntitySupplier<BE> factory)
        {
            super(factory);
        }

        @Override
        protected LimaBlockEntityType<BE> build(BlockEntitySupplier<BE> factory, Set<Block> validBlocks)
        {
            return new LimaBlockEntityType<>(factory, validBlocks);
        }
    }
}