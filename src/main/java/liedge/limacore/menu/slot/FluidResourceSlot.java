package liedge.limacore.menu.slot;

import com.google.common.base.Predicates;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import org.jspecify.annotations.Nullable;

public class FluidResourceSlot extends LimaFluidSlot
{
    private final ResourceHandler<FluidResource> handler;
    private final int resourceIndex;

    private boolean allowPlace = true;

    public FluidResourceSlot(int x, int y, int slotIndex, ResourceHandler<FluidResource> handler, int resourceIndex)
    {
        super(x, y, slotIndex);
        this.handler = handler;
        this.resourceIndex = resourceIndex;
    }

    public FluidResourceSlot setAllowPlace(boolean allowPlace)
    {
        this.allowPlace = allowPlace;
        return this;
    }

    @Override
    public FluidStack getFluid()
    {
        return getFluidResource().toStack(getAmount());
    }

    @Override
    public FluidResource getFluidResource()
    {
        return handler.getResource(resourceIndex);
    }

    @Override
    public int getAmount()
    {
        return handler.getAmountAsInt(resourceIndex);
    }

    @Override
    public int getCapacity(FluidResource resource)
    {
        return handler.getCapacityAsInt(resourceIndex, resource);
    }

    @Override
    public int getCapacity()
    {
        return handler.getCapacityAsInt(resourceIndex, FluidResource.EMPTY);
    }

    @Override
    public boolean mayPlace(FluidResource resource)
    {
        return allowPlace && handler.isValid(resourceIndex, resource);
    }

    @Override
    public @Nullable ResourceStack<FluidResource> fillSlotFromItem(ResourceHandler<FluidResource> carriedFluids)
    {
        return ResourceHandlerUtil.moveFirst(carriedFluids, createSingeSlotHandler(), this::mayPlace, getCapacity(), null);
    }

    @Override
    public @Nullable ResourceStack<FluidResource> drainSlotIntoItem(ResourceHandler<FluidResource> carriedFluids)
    {
        return ResourceHandlerUtil.moveFirst(createSingeSlotHandler(), carriedFluids, Predicates.alwaysTrue(), getCapacity(), null);
    }

    public ResourceHandler<FluidResource> getResourceHandler()
    {
        return handler;
    }

    public ResourceHandler<FluidResource> createSingeSlotHandler()
    {
        return RangedResourceHandler.ofSingleIndex(handler, resourceIndex);
    }
}