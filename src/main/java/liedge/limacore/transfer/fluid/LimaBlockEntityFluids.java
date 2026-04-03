package liedge.limacore.transfer.fluid;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.transfer.ExternalResourceHandler;
import liedge.limacore.transfer.VariableRateTransferHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public class LimaBlockEntityFluids extends FluidStacksResourceHandler implements VariableRateTransferHandler
{
    private final FluidHolderBlockEntity blockEntity;
    private final BlockContentsType contentsType;
    private final int minSize;

    private int transferRate;

    public LimaBlockEntityFluids(FluidHolderBlockEntity blockEntity, BlockContentsType contentsType, int size)
    {
        super(size, blockEntity.getBaseFluidCapacity(contentsType));
        this.blockEntity = blockEntity;
        this.contentsType = contentsType;
        this.minSize = size;

        this.transferRate = blockEntity.getBaseFluidTransferRate(contentsType);
    }

    public ResourceHandler<FluidResource> createIOWrapper(IOAccess topLevelAccess)
    {
        return new ExternalWrapper(this, topLevelAccess);
    }

    public void syncTanks(DataWatcherHolder.DataWatcherCollector collector)
    {
        for (int i = 0; i < size(); i++)
        {
            final int index = i;
            LimaDataWatcher<FluidStack> watcher = AutomaticDataWatcher.keepSynced(
                    LimaCoreNetworkSerializers.FLUID_STACK,
                    () -> stacks.get(index),
                    fs -> stacks.set(index, fs));

            collector.register(watcher);
        }
    }

    public LimaDataWatcher<Integer> syncCapacity()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getCapacity, this::setCapacity);
    }

    public void keepHandlerSynced(DataWatcherHolder.DataWatcherCollector collector)
    {
        syncTanks(collector);
        collector.register(syncCapacity());
        collector.register(syncTransferRate());
    }

    public int getCapacity()
    {
        return capacity;
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }

    @Override
    public int getTransferRate()
    {
        return transferRate;
    }

    @Override
    public void setTransferRate(int transferRate)
    {
        this.transferRate = transferRate;
    }

    @Override
    public boolean isValid(int index, FluidResource resource)
    {
        return blockEntity.isFluidValid(contentsType, index, resource);
    }

    @Override
    protected void onContentsChanged(int index, FluidStack previousContents)
    {
        blockEntity.onFluidChanged(contentsType, index, previousContents);
    }

    @Override
    public void deserialize(ValueInput input)
    {
        input.read(VALUE_IO_KEY, codec).ifPresent(list ->
        {
            int size = Math.max(list.size(), this.minSize);
            NonNullList<FluidStack> fixedStacks = NonNullList.withSize(size, FluidStack.EMPTY);

            for (int i = 0; i < list.size(); i++)
            {
                fixedStacks.set(i, list.get(i));
            }

            setStacks(fixedStacks);
        });
    }

    private static class ExternalWrapper extends ExternalResourceHandler<FluidResource, LimaBlockEntityFluids>
    {
        ExternalWrapper(LimaBlockEntityFluids base, IOAccess topLevelAccess)
        {
            super(base, topLevelAccess);
        }

        @Override
        protected boolean canInsert(LimaBlockEntityFluids base, int index, FluidResource resource, IOAccess topLevelAccess)
        {
            return topLevelAccess.allowsInput() && base.blockEntity.getResourceLevelFluidIO(base.contentsType, index, resource).allowsInput();
        }

        @Override
        protected boolean canExtract(LimaBlockEntityFluids base, int index, FluidResource resource, IOAccess topLevelAccess)
        {
            return topLevelAccess.allowsOutput() && base.blockEntity.getResourceLevelFluidIO(base.contentsType, index, resource).allowsOutput();
        }

        @Override
        protected int getTransferLimit(LimaBlockEntityFluids base)
        {
            return base.transferRate;
        }
    }
}