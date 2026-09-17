package liedge.limacore.transfer.energy;

import liedge.limacore.blockentity.IOAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.IntSupplier;

public class ExternalAccessEnergyHandler extends LimitingEnergyHandler
{
    private final IOAccess access;

    public ExternalAccessEnergyHandler(EnergyHandler delegate, IntSupplier transferLimit, IOAccess access)
    {
        super(delegate, transferLimit);
        this.access = access;
    }

    public ExternalAccessEnergyHandler(EnergyHandler delegate, int transferLimit, IOAccess access)
    {
        super(delegate, transferLimit);
        this.access = access;
    }

    @Override
    public int insert(int amount, TransactionContext transaction)
    {
        return access.allowsInput() ? super.insert(amount, transaction) : 0;
    }

    @Override
    public int extract(int amount, TransactionContext transaction)
    {
        return access.allowsOutput() ? super.extract(amount, transaction) : 0;
    }
}