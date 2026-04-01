package liedge.limacore.transfer.energy;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import net.minecraft.core.component.DataComponentMap;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

public final class AdjustableBlockEntityEnergy extends SimpleEnergyHandler implements VariableEnergyHandler
{
    private final EnergyHolderBlockEntity blockEntity;
    private int transferRate;

    public AdjustableBlockEntityEnergy(EnergyHolderBlockEntity blockEntity)
    {
        super(blockEntity.getBaseEnergyCapacity());
        this.blockEntity = blockEntity;
        this.transferRate = blockEntity.getBaseEnergyTransferRate();
    }

    public void writeComponents(MutableDataComponentHolder dataHolder)
    {
        dataHolder.set(LimaCoreDataComponents.ENERGY, getAmountAsInt());
        dataHolder.set(LimaCoreDataComponents.ENERGY_CAPACITY, getCapacityAsInt());
        dataHolder.set(LimaCoreDataComponents.ENERGY_TRANSFER_RATE, getTransferRate());
    }

    public void writeComponents(DataComponentMap.Builder builder)
    {
        builder.set(LimaCoreDataComponents.ENERGY, getAmountAsInt());
        builder.set(LimaCoreDataComponents.ENERGY_CAPACITY, getCapacityAsInt());
        builder.set(LimaCoreDataComponents.ENERGY_TRANSFER_RATE, getTransferRate());
    }

    public LimaDataWatcher<Integer> syncEnergyStored()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getAmountAsInt, this::set);
    }

    public LimaDataWatcher<Integer> syncCapacity()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getCapacityAsInt, this::setCapacity);
    }

    public void keepAllPropertiesSynced(DataWatcherHolder.DataWatcherCollector collector)
    {
        collector.register(syncEnergyStored());
        collector.register(syncCapacity());
        collector.register(syncTransferRate());
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
    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
        this.maxInsert = capacity;
        this.maxExtract = capacity;
    }

    @Override
    protected void onEnergyChanged(int previousAmount)
    {
        blockEntity.onEnergyChanged(previousAmount);
    }
}