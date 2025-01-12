package liedge.limacore.inventory.menu;

import liedge.limacore.blockentity.LimaBlockEntityAccess;
import liedge.limacore.util.LimaBlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

/**
 * Convenience menu type for game objects that implement {@link LimaBlockEntityAccess}.
 */
public class BlockEntityAccessMenuType<BE extends LimaBlockEntityAccess, M extends LimaMenu<BE>> extends LimaMenuType<BE, M>
{
    public static <BE extends LimaBlockEntityAccess, M extends LimaMenu<BE>> BlockEntityAccessMenuType<BE, M> create(ResourceLocation id, Class<BE> contextClass, MenuFactory<BE, M> factory)
    {
        return new BlockEntityAccessMenuType<>(id, contextClass, factory);
    }

    private BlockEntityAccessMenuType(ResourceLocation registryId, Class<BE> contextClass, MenuFactory<BE, M> factory)
    {
        super(registryId, contextClass, factory);
    }

    @Override
    public void encodeContext(BE menuContext, RegistryFriendlyByteBuf net)
    {
        net.writeBlockPos(menuContext.getAsLimaBlockEntity().getBlockPos());
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