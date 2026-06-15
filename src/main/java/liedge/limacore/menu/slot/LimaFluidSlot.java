package liedge.limacore.menu.slot;

import liedge.limacore.transfer.fluid.LimaFluidResourceHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class LimaFluidSlot
{
    private final LimaFluidResourceHandler handler;
    private final int x;
    private final int y;
    private final int slotIndex;
    private final int resourceIndex;
    private final boolean allowPlace;

    public LimaFluidSlot(LimaFluidResourceHandler handler, int x, int y, int slotIndex, int resourceIndex, boolean allowPlace)
    {
        this.handler = handler;
        this.x = x;
        this.y = y;
        this.slotIndex = slotIndex;
        this.resourceIndex = resourceIndex;
        this.allowPlace = allowPlace;
    }

    public LimaFluidResourceHandler getFluidHandler()
    {
        return handler;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getSlotIndex()
    {
        return slotIndex;
    }

    public int getResourceIndex()
    {
        return resourceIndex;
    }

    public boolean allowsPlacement()
    {
        return allowPlace;
    }

    public FluidResource getFluidResource()
    {
        return handler.getResource(resourceIndex);
    }

    public FluidStack getFluid()
    {
        return getFluidResource().toStack(handler.getAmountAsInt(resourceIndex));
    }

    public int getCapacity()
    {
        return handler.getCapacity();
    }

    public boolean mayPlace(FluidResource resource)
    {
        return allowPlace && handler.isValid(resourceIndex, resource);
    }

    public boolean canCreateCloneBucket(Player player)
    {
        return player.hasInfiniteMaterials() && !getFluidResource().isEmpty();
    }

    public boolean canClear(Player player, ItemStack cursorItem)
    {
        return false;
    }

    public void clearFluid()
    {
        handler.set(resourceIndex, FluidResource.EMPTY, 0);
    }
}