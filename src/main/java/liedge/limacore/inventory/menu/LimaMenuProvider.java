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
    LimaMenuType<?, ?> getMenuType();

    default void openMenuScreen(Player player)
    {
        if (player instanceof ServerPlayer serverPlayer)
        {
            serverPlayer.openMenu(this, net -> getMenuType().encodeUncheckedContext(this, net));
        }
        else
        {
            LimaCore.LOGGER.warn("Tried to open menu screen of type '{}' on the client", LimaRegistryUtil.getNonNullRegistryKey(getMenuType(), BuiltInRegistries.MENU));
        }
    }

    @Override
    default AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return getMenuType().tryCreateMenu(containerId, inventory, this);
    }

    @Override
    default Component getDisplayName()
    {
        return getMenuType().translate();
    }
}