package liedge.limacore.transfer;

import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.IntSupplier;

public class LimitingResourceHandler<T extends Resource> extends DelegatingResourceHandler<T>
{
    public static <T extends Resource> ResourceHandler<T> create(ResourceHandler<T> delegate, int transferLimit)
    {
        if (transferLimit == Integer.MAX_VALUE)
            return delegate;
        else
            return new LimitingResourceHandler<>(delegate, () -> transferLimit);
    }

    private final IntSupplier transferLimit;

    public LimitingResourceHandler(ResourceHandler<T> delegate, IntSupplier transferLimit)
    {
        super(delegate);
        this.transferLimit = transferLimit;
    }

    protected final int getTransferLimit()
    {
        return transferLimit.getAsInt();
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction)
    {
        return super.insert(index, resource, clampAmount(amount), transaction);
    }

    @Override
    public int insert(T resource, int amount, TransactionContext transaction)
    {
        return super.insert(resource, clampAmount(amount), transaction);
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction)
    {
        return super.extract(index, resource, clampAmount(amount), transaction);
    }

    @Override
    public int extract(T resource, int amount, TransactionContext transaction)
    {
        return super.extract(resource, clampAmount(amount), transaction);
    }

    private int clampAmount(int amount)
    {
        return Math.min(amount, getTransferLimit());
    }
}