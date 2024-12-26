package liedge.limacore.inventory.menu;

import liedge.limacore.LimaCore;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface LimaMenuProvider extends MenuProvider
{
    static <CTX, M extends LimaMenu<CTX>> void openStandaloneMenu(Player player, LimaMenuType<CTX, M> menuType, CTX menuContext)
    {
        LimaMenuProvider provider = new LimaMenuProvider()
        {
            @Override
            public LimaMenuType<?, ?> getMenuType()
            {
                return menuType;
            }

            @Override
            public CTX getOrCreateMenuContext()
            {
                return menuContext;
            }
        };

        provider.openMenuScreen(player);
    }

    LimaMenuType<?, ?> getMenuType();

    default Object getOrCreateMenuContext()
    {
        return this;
    }

    default void openMenuScreen(Player player)
    {
        if (player instanceof ServerPlayer serverPlayer)
        {
            serverPlayer.openMenu(this, net -> getMenuType().tryEncodeContext(getOrCreateMenuContext(), net));
        }
        else {
            LimaCore.LOGGER.warn("Tried to open menu screen of type '{}' on the client", LimaRegistryUtil.getNonNullRegistryId(getMenuType(), BuiltInRegistries.MENU));
        }
    }

    @Override
    default AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return getMenuType().tryCreateMenu(containerId, inventory, getOrCreateMenuContext());
    }

    @Override
    default Component getDisplayName()
    {
        return getMenuType().getMenuTitle(getOrCreateMenuContext());
    }
}