package liedge.limacore.transfer.energy;

import net.neoforged.neoforge.transfer.energy.InfiniteEnergyHandler;

public final class InfiniteBlockEntityEnergy extends InfiniteEnergyHandler implements VariableEnergyHandler
{
    public static final InfiniteBlockEntityEnergy INSTANCE = new InfiniteBlockEntityEnergy();

    private InfiniteBlockEntityEnergy() { }

    @Override
    public int getTransferRate()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setTransferRate(int transferRate) { }

    @Override
    public void setCapacity(int capacity) { }
}