package liedge.limacore.transfer.energy;

import net.neoforged.neoforge.transfer.energy.InfiniteEnergyHandler;

public final class LimaInfiniteEnergyHandler extends InfiniteEnergyHandler implements LimaEnergyHandler
{
    public static final LimaInfiniteEnergyHandler INSTANCE = new LimaInfiniteEnergyHandler();

    private LimaInfiniteEnergyHandler() { }

    @Override
    public int getTransferRate()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setTransferRate(int transferRate) { }
}