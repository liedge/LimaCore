package liedge.limacore.menu.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public abstract class LimaFluidSlot
{
    private final int x;
    private final int y;
    private final int slotIndex;

    protected LimaFluidSlot(int x, int y, int slotIndex)
    {
        this.x = x;
        this.y = y;
        this.slotIndex = slotIndex;
    }

    // Basic properties

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

    public abstract FluidStack getFluid();

    public abstract void setFluid(FluidStack stack);

    public abstract FluidResource getFluidResource();

    public abstract int getAmount();

    public abstract int getCapacity();

    public abstract boolean mayPlace(FluidResource resource);

    // Menu functions

    public abstract boolean fillSlotFromItem(ResourceHandler<FluidResource> carriedFluids);

    public abstract boolean drainSlotIntoItem(ResourceHandler<FluidResource> carriedFluids);

    public boolean canCreateCloneBucket(Player player)
    {
        return player.hasInfiniteMaterials() && !getFluidResource().isEmpty();
    }

    public boolean canClear(Player player, ItemStack carriedItem)
    {
        return false;
    }
}