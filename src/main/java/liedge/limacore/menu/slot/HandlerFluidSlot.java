package liedge.limacore.menu.slot;

import com.google.common.base.Predicates;
import liedge.limacore.transfer.fluid.LimaBlockEntityFluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class HandlerFluidSlot extends LimaFluidSlot
{
    private final LimaBlockEntityFluids handler;
    private final int resourceIndex;

    private boolean allowPlace = true;

    public HandlerFluidSlot(int x, int y, int slotIndex, LimaBlockEntityFluids handler, int resourceIndex)
    {
        super(x, y, slotIndex);
        this.handler = handler;
        this.resourceIndex = resourceIndex;
    }

    public HandlerFluidSlot setAllowPlace(boolean allowPlace)
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
    public void setFluid(FluidStack stack)
    {
        handler.set(resourceIndex, FluidResource.of(stack), stack.getAmount());
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
    public int getCapacity()
    {
        return handler.getCapacity();
    }

    @Override
    public boolean mayPlace(FluidResource resource)
    {
        return allowPlace && handler.isValid(resourceIndex, resource);
    }

    @Override
    public boolean fillSlotFromItem(ResourceHandler<FluidResource> carriedFluids)
    {
        int inserted = ResourceHandlerUtil.move(carriedFluids, indexHandler(), this::mayPlace, getCapacity(), null);
        return inserted > 0;
    }

    @Override
    public boolean drainSlotIntoItem(ResourceHandler<FluidResource> carriedFluids)
    {
        int extracted = ResourceHandlerUtil.move(indexHandler(), carriedFluids, Predicates.alwaysTrue(), getCapacity(), null);
        return extracted > 0;
    }

    private ResourceHandler<FluidResource> indexHandler()
    {
        return RangedResourceHandler.ofSingleIndex(handler, resourceIndex);
    }
}