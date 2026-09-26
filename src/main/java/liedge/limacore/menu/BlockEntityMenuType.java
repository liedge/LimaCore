package liedge.limacore.menu;

import liedge.limacore.blockentity.LimaBlockEntityAccess;
import liedge.limacore.util.LimaCoreObjects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.Nullable;

public class BlockEntityMenuType<BE extends LimaBlockEntityAccess, M extends BlockEntityMenu<BE>> extends LimaMenuType<M>
{
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