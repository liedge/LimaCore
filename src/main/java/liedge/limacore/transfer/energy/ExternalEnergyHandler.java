package liedge.limacore.transfer.energy;

import liedge.limacore.blockentity.IOAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class ExternalEnergyHandler implements EnergyHandler
{
    private final LimaEnergyHandler base;
    private final IOAccess topLevelAccess;

    public ExternalEnergyHandler(LimaEnergyHandler base, IOAccess topLevelAccess)
    {
        this.base = base;
        this.topLevelAccess = topLevelAccess;
    }

    @Override
    public long getAmountAsLong()
    {
        return base.getAmountAsLong();
    }

    @Override
    public long getCapacityAsLong()
    {
        return base.getCapacityAsLong();
    }

    @Override
    public int insert(int amount, TransactionContext transaction)
    {
        if (topLevelAccess.allowsInput())
        {
            int toInsert = Math.min(amount, base.getTransferRate());
            return base.insert(toInsert, transaction);
        }

        return 0;
    }

    @Override
    public int extract(int amount, TransactionContext transaction)
    {
        if (topLevelAccess.allowsOutput())
        {
            int toExtract = Math.min(amount, base.getTransferRate());
            return base.extract(toExtract, transaction);
        }

        return 0;
    }
}