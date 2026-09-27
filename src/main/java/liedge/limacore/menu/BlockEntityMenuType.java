package liedge.limacore.menu;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import liedge.limacore.blockentity.LimaBlockEntityAccess;
import liedge.limacore.util.LimaCoreObjects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public final class BlockEntityMenuType<BE extends LimaBlockEntityAccess, M extends BlockEntityMenu<BE>> extends LimaMenuType<M>
{
    public static final Codec<BlockEntityMenuType<?, ?>> CODEC = BuiltInRegistries.MENU.byNameCodec().comapFlatMap(o -> {
        if (o instanceof BlockEntityMenuType<?,?> type)
            return DataResult.success(type);
        else
            return DataResult.error(() -> "Not a block entity menu type.");
    }, Function.identity());

    public static <BE extends LimaBlockEntityAccess, M extends BlockEntityMenu<BE>> BlockEntityMenuType<BE, M> create(Identifier id, Class<BE> beClass, TypedFactory<BE, M> factory)
    {
        return new BlockEntityMenuType<>(id, beClass, factory);
    }

    private final Class<BE> contextClass;
    private final TypedFactory<BE, M> factory;

    private BlockEntityMenuType(Identifier id, Class<BE> contextClass, TypedFactory<BE, M> factory)
    {
        super(id);
        this.contextClass = contextClass;
        this.factory = factory;
    }

    @Override
    public M create(int containerId, Inventory inventory, RegistryFriendlyByteBuf net)
    {
        return factory.create(this, containerId, inventory, BlockEntityMenu.decodeBlockEntity(net, inventory, contextClass));
    }

    public @Nullable M create(int containerId, Inventory inventory, LimaBlockEntityAccess beAccess)
    {
        BE blockEntity = LimaCoreObjects.tryCast(contextClass, beAccess);
        return blockEntity != null ? factory.create(this, containerId, inventory, blockEntity) : null;
    }

    @FunctionalInterface
    public interface TypedFactory<BE extends LimaBlockEntityAccess, M extends BlockEntityMenu<BE>>
    {
        M create(MenuType<?> type, int containerId, Inventory inventory, BE blockEntity);
    }
}