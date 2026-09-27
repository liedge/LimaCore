package liedge.limacore.blockentity;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import liedge.limacore.menu.BlockEntityMenuType;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public final class LimaBlockEntityType<BE extends LimaBlockEntity> extends BlockEntityType<BE> implements DataComponentHolder
{
    public static <BE extends LimaBlockEntity> LimaBlockEntityType.Builder<BE> builder(ResourceKey<BlockEntityType<?>> key, BlockEntitySupplier<BE> factory)
    {
        return new LimaBlockEntityType.Builder<>(key, factory);
    }

    public static <BE extends LimaBlockEntity> LimaBlockEntityType.Builder<BE> builder(Identifier id, BlockEntitySupplier<BE> factory)
    {
        return builder(ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, id), factory);
    }

    private LimaBlockEntityType(BlockEntitySupplier<BE> factory, Set<Block> validBlocks)
    {
        super(factory, validBlocks);
    }

    public @Nullable <T> T getDataMap(DataMapType<BlockEntityType<?>, T> dataMapType)
    {
        return LimaRegistryUtil.builtInHolder(this).getData(dataMapType);
    }

    public <T> T getDataMapOrDefault(DataMapType<BlockEntityType<?>, T> dataMapType, T fallback)
    {
        return Objects.requireNonNullElse(getDataMap(dataMapType), fallback);
    }

    @Override
    public DataComponentMap getComponents()
    {
        return LimaRegistryUtil.builtInHolder(this).components();
    }

    public static final class Builder<BE extends LimaBlockEntity>
    {
        private final ResourceKey<BlockEntityType<?>> key;
        private final BlockEntitySupplier<BE> factory;
        private final ObjectSet<Block> validBlocks = new ObjectOpenHashSet<>();
        private DataComponentInitializers.Initializer<BlockEntityType<?>> components = (_, _, _) -> { };

        private Builder(ResourceKey<BlockEntityType<?>> key, BlockEntitySupplier<BE> factory)
        {
            this.key = key;
            this.factory = factory;
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

        public <T> Builder<BE> component(DataComponentType<T> type, T value)
        {
            components = components.add(type, value);
            return this;
        }

        public <T> Builder<BE> component(Supplier<? extends DataComponentType<T>> typeSupplier, T value)
        {
            return component(typeSupplier.get(), value);
        }

        public <T> Builder<BE> delayedComponent(DataComponentType<T> type, DataComponentInitializers.SingleComponentInitializer<T> initializer)
        {
            components = components.andThen(initializer.asInitializer(type));
            return this;
        }

        public <T> Builder<BE> delayedComponent(Supplier<? extends DataComponentType<T>> typeSupplier, DataComponentInitializers.SingleComponentInitializer<T> initializer)
        {
            return delayedComponent(typeSupplier.get(), initializer);
        }

        public Builder<BE> hasMenu(Holder<MenuType<?>> holder)
        {
            return delayedComponent(LimaCoreDataComponents.BLOCK_MENU, _ -> {
                if (!(holder.value() instanceof BlockEntityMenuType<?,?> menuType))
                    throw new IllegalStateException(LimaRegistryUtil.getNonNullRegistryId(holder) + " is not a block entity menu type");

                return menuType;
            });
        }

        public LimaBlockEntityType<BE> build()
        {
            Preconditions.checkState(!validBlocks.isEmpty(), "Valid blocks cannot be empty.");

            BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.add(key, components);
            return new LimaBlockEntityType<>(factory, ObjectSets.unmodifiable(validBlocks));
        }
    }
}