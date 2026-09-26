package liedge.limacore.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;

public abstract class LimaMenuType<M extends LimaMenu> extends MenuType<M>
{
    public static <M extends LimaMenu> LimaMenuType<M> create(Identifier id, IContainerFactory<M> factory)
    {
        return new SimpleType<>(id, factory);
    }

    private final String descriptionId;

    LimaMenuType(Identifier id)
    {
        super((_, _) -> {
            throw new UnsupportedOperationException("Unsupported menu creation method");
        }, FeatureFlags.DEFAULT_FLAGS);

        this.descriptionId = id.toLanguageKey("container");
    }

    public String getDescriptionId()
    {
        return descriptionId;
    }

    @Deprecated
    @Override
    public final M create(int containerId, Inventory inventory)
    {
        return super.create(containerId, inventory);
    }

    @Override
    public abstract M create(int containerId, Inventory inventory, RegistryFriendlyByteBuf net);

    private static class SimpleType<M extends LimaMenu> extends LimaMenuType<M>
    {
        private final IContainerFactory<M> factory;

        SimpleType(Identifier id, IContainerFactory<M> factory)
        {
            super(id);
            this.factory = factory;
        }

        @Override
        public M create(int containerId, Inventory inventory, RegistryFriendlyByteBuf net)
        {
            return factory.create(containerId, inventory, net);
        }
    }
}