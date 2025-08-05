package liedge.limacore.menu;

import liedge.limacore.blockentity.LimaBlockEntity;
import net.minecraft.network.chat.Component;

public final class BlockEntityMenuProvider implements LimaMenuProvider
{
    private final BlockEntityMenuType<?, ?> menuType;
    private final LimaBlockEntity blockEntity;
    private final boolean closeClientContainer;

    public BlockEntityMenuProvider(BlockEntityMenuType<?, ?> menuType, LimaBlockEntity blockEntity, boolean closeClientContainer)
    {
        this.menuType = menuType;
        this.blockEntity = blockEntity;
        this.closeClientContainer = closeClientContainer;
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return menuType;
    }

    @Override
    public Object context()
    {
        return blockEntity;
    }

    @Override
    public Component getDisplayName()
    {
        return blockEntity.getMenuTitle(menuType);
    }

    @Override
    public boolean shouldTriggerClientSideContainerClosingOnOpen()
    {
        return closeClientContainer;
    }
}