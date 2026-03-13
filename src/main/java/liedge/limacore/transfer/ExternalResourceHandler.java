package liedge.limacore.transfer;

import liedge.limacore.blockentity.IOAccess;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public abstract class ExternalResourceHandler<R extends Resource, T extends ResourceHandler<R>> implements ResourceHandler<R>
{
    private final T base;
    private final IOAccess topLevelAccess;

    protected ExternalResourceHandler(T base, IOAccess topLevelAccess)
    {
        this.base = base;
        this.topLevelAccess = topLevelAccess;
    }

    protected abstract boolean canInsert(T base, int index, R resource, IOAccess topLevelAccess);

    protected abstract boolean canExtract(T base, int index, R resource, IOAccess topLevelAccess);

    protected abstract int getTransferLimit(T base);

    @Override
    public int size()
    {
        return base.size();
    }

    @Override
    public R getResource(int index)
    {
        return base.getResource(index);
    }

    @Override
    public long getAmountAsLong(int index)
    {
        return base.getAmountAsLong(index);
    }

    @Override
    public long getCapacityAsLong(int index, R resource)
    {
        return base.getCapacityAsLong(index, resource);
    }

    @Override
    public boolean isValid(int index, R resource)
    {
        return base.isValid(index, resource);
    }

    @Override
    public int insert(int index, R resource, int amount, TransactionContext transaction)
    {
        if (canInsert(base, index, resource, topLevelAccess))
        {
            int toInsert = Math.min(amount, getTransferLimit(base));
            return base.insert(index, resource, toInsert, transaction);
        }

        return 0;
    }

    @Override
    public int extract(int index, R resource, int amount, TransactionContext transaction)
    {
        if (canExtract(base, index, resource, topLevelAccess))
        {
            int toExtract = Math.min(amount, getTransferLimit(base));
            return base.extract(index, resource, toExtract, transaction);
        }

        return 0;
    }

}