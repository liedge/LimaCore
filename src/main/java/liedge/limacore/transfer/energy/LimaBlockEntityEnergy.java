package liedge.limacore.transfer.energy;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

public final class LimaBlockEntityEnergy extends SimpleEnergyHandler implements LimaEnergyHandler
{
    private final EnergyHolderBlockEntity blockEntity;
    private int transferRate;

    public LimaBlockEntityEnergy(EnergyHolderBlockEntity blockEntity)
    {
        super(blockEntity.getBaseEnergyCapacity());
        this.blockEntity = blockEntity;
        this.transferRate = blockEntity.getBaseEnergyTransferRate();
    }

    @Override
    public void readComponents(DataComponentGetter components)
    {
        Integer energy = components.get(LimaCoreDataComponents.ENERGY);
        if (energy != null) set(energy);

        Integer capacity = components.get(LimaCoreDataComponents.ENERGY_CAPACITY);
        if (capacity != null) setCapacity(capacity);

        Integer transferRate = components.get(LimaCoreDataComponents.ENERGY_TRANSFER_RATE);
        if (transferRate != null) setTransferRate(transferRate);
    }

    @Override
    public void writeComponents(DataComponentMap.Builder components)
    {
        components.set(LimaCoreDataComponents.ENERGY, getAmountAsInt());
        components.set(LimaCoreDataComponents.ENERGY_CAPACITY, getCapacityAsInt());
        components.set(LimaCoreDataComponents.ENERGY_TRANSFER_RATE, getTransferRate());
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output)
    {
        output.discard(LimaCommonConstants.KEY_ENERGY_CONTAINER);
    }

    @Override
    public void syncAllProperties(DataWatcherHolder.DataWatcherCollector collector)
    {
        collector.register(syncEnergy());
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