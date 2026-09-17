package liedge.limacore.transfer.energy;

import net.neoforged.neoforge.transfer.energy.DelegatingEnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.IntSupplier;

public class LimitingEnergyHandler extends DelegatingEnergyHandler
{
    private final IntSupplier transferLimit;

    public LimitingEnergyHandler(EnergyHandler delegate, IntSupplier transferLimit)
    {
        super(delegate);
        this.transferLimit = transferLimit;
    }

    public LimitingEnergyHandler(EnergyHandler delegate, int transferLimit)
    {
        this(delegate, () -> transferLimit);
    }

    @Override
    public int insert(int amount, TransactionContext transaction)
    {
        int toInsert = Math.min(amount, transferLimit.getAsInt());
        return super.insert(toInsert, transaction);
    }

    @Override
    public int extract(int amount, TransactionContext transaction)
    {
        int toExtract = Math.min(amount, transferLimit.getAsInt());
        return super.extract(toExtract, transaction);
    }
}