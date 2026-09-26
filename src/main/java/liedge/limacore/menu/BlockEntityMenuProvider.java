package liedge.limacore.menu;

import liedge.limacore.blockentity.LimaBlockEntityAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public final class BlockEntityMenuProvider implements MenuProvider
{
    private final BlockEntityMenuType<?, ?> type;
    private final LimaBlockEntityAccess blockEntity;
    private final Component displayName;
    private final boolean closeClientContainer;

    public BlockEntityMenuProvider(BlockEntityMenuType<?, ?> type, LimaBlockEntityAccess blockEntity, @Nullable Component displayName, boolean closeClientContainer)
    {
        this.type = type;
        this.blockEntity = blockEntity;
        this.displayName = displayName != null ? displayName : blockEntity.getAsLimaBlockEntity().getMenuTitle(type);
        this.closeClientContainer = closeClientContainer;
    }

    public BlockEntityMenuProvider(Supplier<? extends BlockEntityMenuType<?, ?>> typeSupplier, LimaBlockEntityAccess blockEntity, @Nullable Component displayName, boolean closeClientContainer)
    {
        this(typeSupplier.get(), blockEntity, displayName, closeClientContainer);
    }

    @Override
    public Component getDisplayName()
    {
        return displayName;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return type.create(containerId, inventory, blockEntity);
    }

    @Override
    public boolean shouldTriggerClientSideContainerClosingOnOpen()
    {
        return closeClientContainer;
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(blockEntity.getBlockPos());
    }
}