package liedge.limacore.menu;

import liedge.limacore.LimaCore;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface LimaMenuProvider extends MenuProvider
{
    static <CTX, M extends LimaMenu<CTX>> LimaMenuProvider create(LimaMenuType<CTX, M> type, CTX context, @Nullable Component title, boolean closeClientContainer)
    {
        return new LimaMenuProvider()
        {
            @Override
            public LimaMenuType<?, ?> getMenuType()
            {
                return type;
            }

            @Override
            public CTX context()
            {
                return context;
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
            {
                return type.createMenu(containerId, inventory, context);
            }

            @Override
            public Component getDisplayName()
            {
                return title != null ? title : LimaMenuProvider.super.getDisplayName();
            }

            @Override
            public boolean shouldTriggerClientSideContainerClosingOnOpen()
            {
                return closeClientContainer;
            }
        };
    }

    static <CTX, M extends LimaMenu<CTX>> LimaMenuProvider create(LimaMenuType<CTX, M> type, CTX context, @Nullable Component title)
    {
        return create(type, context, title, true);
    }

    LimaMenuType<?, ?> getMenuType();

    Object context();

    default void openMenuScreen(Player player)
    {
        if (player instanceof ServerPlayer serverPlayer)
        {
            serverPlayer.openMenu(this, net -> getMenuType().tryEncodeContext(context(), net));
        }
        else
        {
            LimaCore.LOGGER.warn("Tried to open menu screen of type '{}' on the client", LimaRegistryUtil.getNonNullRegistryId(getMenuType(), BuiltInRegistries.MENU));
        }
    }

    @Override
    default AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return getMenuType().tryCreateMenu(containerId, inventory, context());
    }

    @Override
    default Component getDisplayName()
    {
        return Objects.requireNonNull(getMenuType().getDefaultTitle(), "Standalone menu types must have a default title.").translate();
    }
}