package liedge.limacore.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public final class StandaloneMenuProvider implements MenuProvider
{
    public static StandaloneMenuProvider create(LimaMenuType<?> type, @Nullable Component displayName, boolean closeClientContainer, MenuConstructor constructor)
    {
        return new StandaloneMenuProvider(displayName != null ? displayName : Component.translatable(type.getDescriptionId()), closeClientContainer, constructor);
    }

    public static StandaloneMenuProvider create(Supplier<? extends LimaMenuType<?>> typeSupplier, @Nullable Component displayName, boolean closeClientContainer, MenuConstructor constructor)
    {
        return create(typeSupplier.get(), displayName, closeClientContainer, constructor);
    }

    private final Component displayName;
    private final  boolean closeClientContainer;
    private final MenuConstructor constructor;

    public StandaloneMenuProvider(Component displayName, boolean closeClientContainer, MenuConstructor constructor)
    {
        this.displayName = displayName;
        this.closeClientContainer = closeClientContainer;
        this.constructor = constructor;
    }

    @Override
    public Component getDisplayName()
    {
        return displayName;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return constructor.createMenu(containerId, inventory, player);
    }

    @Override
    public boolean shouldTriggerClientSideContainerClosingOnOpen()
    {
        return closeClientContainer;
    }
}