package liedge.limacore.transfer.fluid;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.network.sync.*;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.transfer.ExternalAccessResourceHandler;
import liedge.limacore.transfer.LimaTransferUtil;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public class LimaBlockEntityFluids extends FluidStacksResourceHandler
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
        return new ExternalAccessResourceHandler<>(this, this::getTransferRate, topLevelAccess, (index, resource) -> blockEntity.getResourceLevelFluidIO(contentsType, index, resource));
    }

    public void syncTanks(DataWatcherHolder.DataWatcherCollector collector)
    {
        for (int i = 0; i < size(); i++)
        {
            final int index = i;
            ValueTracker<FluidStack> tracker = SimpleValueTracker.create(
                    LimaCoreNetworkSerializers.FLUID_STACK,
                    () -> stacks.get(index),
                    fs -> stacks.set(index, fs))
                    .setAutomatic();

            collector.register(tracker);
        }
    }

    public LimaDataWatcher<Integer> syncCapacity()
    {
        return SimpleValueTracker.create(LimaCoreNetworkSerializers.VAR_INT, this::getCapacity, this::setCapacity);
    }

    public LimaDataWatcher<Integer> syncTransferRate()
    {
        return SimpleValueTracker.create(LimaCoreNetworkSerializers.VAR_INT, this::getTransferRate, this::setTransferRate);
    }

    public void syncAllProperties(DataWatcherHolder.DataWatcherCollector collector)
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

    public int getTransferRate()
    {
        return transferRate;
    }

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
        LimaTransferUtil.loadSizedResources(input, VALUE_IO_KEY, codec, minSize, FluidStack.EMPTY).ifPresent(this::setStacks);
    }
}