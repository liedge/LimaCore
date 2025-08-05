package liedge.limacore.blockentity;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import liedge.limacore.menu.BlockEntityMenuProvider;
import liedge.limacore.menu.BlockEntityMenuType;
import liedge.limacore.menu.LimaMenuProvider;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.MenuType;
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
        return new LimaBlockEntityType<>(factory, Set.of(holder.value()), null);
    }

    public static <BE extends LimaBlockEntity> LimaBlockEntityType<BE> of(BlockEntitySupplier<BE> factory, Holder<Block> holder, Holder<MenuType<?>> menuTypeHolder)
    {
        return new LimaBlockEntityType<>(factory, Set.of(holder.value()), menuTypeHolder);
    }

    public static <BE extends LimaBlockEntity> Builder<BE> builder(BlockEntitySupplier<BE> factory)
    {
        return new Builder<>(factory);
    }

    private final @Nullable Holder<MenuType<?>> menuTypeHolder;

    @SuppressWarnings("ConstantConditions")
    protected LimaBlockEntityType(BlockEntitySupplier<BE> factory, Set<Block> validBlocks, @Nullable Holder<MenuType<?>> menuTypeHolder)
    {
        super(factory, validBlocks, null);
        this.menuTypeHolder = menuTypeHolder;
    }

    public @Nullable <T> T getDataMap(DataMapType<BlockEntityType<?>, T> dataMapType)
    {
        return Objects.requireNonNull(builtInRegistryHolder()).getData(dataMapType);
    }

    public <T> T getDataMapOrDefault(DataMapType<BlockEntityType<?>, T> dataMapType, T fallback)
    {
        return Objects.requireNonNullElse(getDataMap(dataMapType), fallback);
    }

    public @Nullable LimaMenuProvider createMenuProvider(LimaBlockEntity blockEntity, boolean closeClientContainer)
    {
        if (menuTypeHolder != null && menuTypeHolder.value() instanceof BlockEntityMenuType<?,?> menuType)
        {
            return new BlockEntityMenuProvider(menuType, blockEntity, closeClientContainer);
        }

        return null;
    }

    public @Nullable LimaMenuProvider createMenuProvider(LimaBlockEntity blockEntity)
    {
        return createMenuProvider(blockEntity, true);
    }

    public static abstract class AbstractBuilder<BE extends LimaBlockEntity, TYPE extends LimaBlockEntityType<BE>, B extends AbstractBuilder<BE, TYPE, B>>
    {
        protected final BlockEntitySupplier<BE> factory;
        private final ObjectSet<Block> validBlocks = new ObjectOpenHashSet<>();
        protected @Nullable Holder<MenuType<?>> menuTypeHolder;

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

        public B hasMenu(Holder<MenuType<?>> menuTypeHolder)
        {
            this.menuTypeHolder = menuTypeHolder;
            return thisBuilder();
        }

        public abstract TYPE build();

        @SuppressWarnings("unchecked")
        protected B thisBuilder()
        {
            return (B) this;
        }

        protected Set<Block> getValidBlocks()
        {
            Preconditions.checkState(!validBlocks.isEmpty(), "Valid blocks cannot be empty.");
            return ObjectSets.unmodifiable(validBlocks);
        }
    }

    public static class Builder<BE extends LimaBlockEntity> extends AbstractBuilder<BE, LimaBlockEntityType<BE>, Builder<BE>>
    {
        private Builder(BlockEntitySupplier<BE> factory)
        {
            super(factory);
        }

        @Override
        public LimaBlockEntityType<BE> build()
        {
            return new LimaBlockEntityType<>(factory, getValidBlocks(), menuTypeHolder);
        }
    }
}