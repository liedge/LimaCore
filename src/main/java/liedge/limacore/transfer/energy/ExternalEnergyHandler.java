package liedge.limacore.transfer.energy;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.transfer.VariableRateTransferHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class ExternalEnergyHandler implements EnergyHandler
{
    private final EnergyHandler base;
    private final IOAccess access;

    public ExternalEnergyHandler(EnergyHandler base, IOAccess access)
    {
        this.base = base;
        this.access = access;
    }

    private int getTransferRate()
    {
        if (base instanceof VariableRateTransferHandler handler)
        {
            return handler.getTransferRate();
        }
        else
        {
            return Integer.MAX_VALUE;
        }
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
        if (access.allowsInput())
        {
            int toInsert = Math.min(amount, getTransferRate());
            return base.insert(toInsert, transaction);
        }

        return 0;
    }

    @Override
    public int extract(int amount, TransactionContext transaction)
    {
        if (access.allowsOutput())
        {
            int toExtract = Math.min(amount, getTransferRate());
            return base.extract(toExtract, transaction);
        }

        return 0;
    }
}