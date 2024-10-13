package liedge.limacore.inventory.menu;

import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.util.LimaBlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class BlockEntityMenuType<BE extends LimaBlockEntity, M extends LimaMenu<BE>> extends LimaMenuType<BE, M>
{
    public static <BE extends LimaBlockEntity, M extends LimaMenu<BE>> BlockEntityMenuType<BE, M> create(ResourceLocation registryId, Class<BE> contextClass, MenuFactory<BE, M> factory)
    {
        return new BlockEntityMenuType<>(registryId, contextClass, factory);
    }

    private BlockEntityMenuType(ResourceLocation registryId, Class<BE> contextClass, MenuFactory<BE, M> factory)
    {
        super(registryId, contextClass, factory);
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
        return menuContext.canPlayerUse(player);
    }
}