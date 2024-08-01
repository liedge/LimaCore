package liedge.limacore.inventory.menu;

import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class BlockEntityMenuType<BE extends LimaBlockEntity, M extends LimaMenu<BE>> extends LimaMenuType<BE, M>
{
    public BlockEntityMenuType(ResourceLocation registryId, Class<BE> contextClass, Constructor<BE, M> constructor)
    {
        super(registryId, contextClass, constructor);
    }

    @Override
    protected void encodeContext(BE menuContext, FriendlyByteBuf net)
    {
        net.writeBlockPos(menuContext.getBlockPos());
    }

    @Override
    protected BE decodeContext(FriendlyByteBuf net, Inventory inventory)
    {
        BlockPos pos = net.readBlockPos();
        return Objects.requireNonNull(LimaCoreUtil.getSafeBlockEntity(inventory.player.level(), pos, getContextClass()));
    }

    @Override
    public boolean canPlayerKeepUsing(BE menuContext, Player player)
    {
        return menuContext.canPlayerUse(player);
    }
}