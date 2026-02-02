package liedge.limacore.menu;

import liedge.limacore.blockentity.LimaBlockEntityAccess;
import liedge.limacore.lib.Translatable;
import liedge.limacore.util.LimaBlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Menu type specialized for {@link LimaBlockEntityAccess} types.
 */
public class BlockEntityMenuType<BE extends LimaBlockEntityAccess, M extends LimaMenu<BE>> extends LimaMenuType<BE, M>
{
    public static <BE extends LimaBlockEntityAccess, M extends LimaMenu<BE>> BlockEntityMenuType<BE, M> create(Class<BE> contextClass, MenuFactory<BE, M> factory, @Nullable Translatable defaultTitle)
    {
        return new BlockEntityMenuType<>(contextClass, factory, defaultTitle);
    }

    public static <BE extends LimaBlockEntityAccess, M extends LimaMenu<BE>> BlockEntityMenuType<BE, M> create(ResourceLocation id, Class<BE> contextClass, MenuFactory<BE, M> factory)
    {
        return create(contextClass, factory, defaultMenuTitle(id));
    }

    public static <BE extends LimaBlockEntityAccess, M extends LimaMenu<BE>> BlockEntityMenuType<BE, M> create(Class<BE> contextClass, MenuFactory<BE, M> factory)
    {
        return create(contextClass, factory, null);
    }

    private BlockEntityMenuType(Class<BE> contextClass, MenuFactory<BE, M> factory, @Nullable Translatable defaultTitle)
    {
        super(contextClass, factory, defaultTitle);
    }

    @Override
    public void encodeContext(BE menuContext, RegistryFriendlyByteBuf net)
    {
        net.writeBlockPos(menuContext.getBlockPos());
    }

    @Override
    protected BE decodeContext(RegistryFriendlyByteBuf net, Inventory inventory)
    {
        BlockPos pos = net.readBlockPos();
        return Objects.requireNonNull(LimaBlockUtil.getSafeBlockEntity(inventory.player.level(), pos, getContextClass()));
    }

    @Override
    public boolean canPlayerKeepUsing(BE menuContext, Player player)
    {
        return menuContext.getAsLimaBlockEntity().canPlayerUse(player);
    }
}